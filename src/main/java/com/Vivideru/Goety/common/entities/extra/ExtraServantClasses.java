package com.Vivideru.Goety.common.entities.extra;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builds the class of each extra servant: a copy of {@link ExtraServantTemplate} whose superclass is the class of the mob
 * it is made from. The copy keeps every field and method of the template; only its name, its superclass and the target
 * of its super calls change. The class is built the first time the servant is created, once the mob's class is known.
 */
final class ExtraServantClasses {
    private static final String TEMPLATE = Type.getInternalName(ExtraServantTemplate.class);
    private static final String TEMPLATE_SUPER = Type.getInternalName(PathfinderMob.class);
    private static final Map<String, MethodHandle> CONSTRUCTORS = new ConcurrentHashMap<>();
    private static final Map<String, String> FAILURES = new ConcurrentHashMap<>();

    private ExtraServantClasses() {
    }

    /** A new servant, or null when its class cannot be built (it is tried only once). */
    @Nullable
    static Mob create(EntityType<?> type, Level level, ExtraServantDefinition definition) {
        if (FAILURES.containsKey(definition.id())) {
            return null;
        }
        MethodHandle constructor = CONSTRUCTORS.get(definition.id());
        if (constructor == null) {
            synchronized (ExtraServantClasses.class) {
                constructor = CONSTRUCTORS.get(definition.id());
                if (constructor == null) {
                    try {
                        constructor = build(definition, level);
                        CONSTRUCTORS.put(definition.id(), constructor);
                    } catch (Throwable throwable) {
                        FAILURES.put(definition.id(), String.valueOf(throwable.getMessage()));
                        return null;
                    }
                }
            }
        }
        try {
            return (Mob) constructor.invoke(type, level);
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static MethodHandle build(ExtraServantDefinition definition, Level level) throws Exception {
        EntityType<?> parentType = ExtraServants.parentType(definition);
        if (parentType == null) {
            throw new IllegalStateException("there is no entity type " + definition.parent());
        }
        // The class behind an entity type is only known from one of its mobs.
        Entity sample = parentType.create(level);
        if (sample == null) {
            throw new IllegalStateException(definition.parent() + " cannot be created");
        }
        Class<?> parent = sample.getClass();
        sample.discard();
        check(definition, parent);
        String name = Type.getInternalName(ExtraServantClasses.class).replace("ExtraServantClasses", "ExtraServant_" + definition.id());
        byte[] bytes = copyTemplate(name, Type.getInternalName(parent));
        Class<?> generated = MethodHandles.lookup().defineClass(bytes);
        return MethodHandles.lookup().findConstructor(generated, MethodType.methodType(void.class, EntityType.class, Level.class));
    }

    /** Refuses the mobs the template cannot become. */
    private static void check(ExtraServantDefinition definition, Class<?> parent) {
        if (!PathfinderMob.class.isAssignableFrom(parent)) {
            throw new IllegalStateException(parent.getName() + " is not a PathfinderMob (a mob that walks to its targets)");
        }
        if (Modifier.isFinal(parent.getModifiers())) {
            throw new IllegalStateException(parent.getName() + " is final and cannot be extended");
        }
        if (!Modifier.isPublic(parent.getModifiers())) {
            throw new IllegalStateException(parent.getName() + " is not public");
        }
        Constructor<?> constructor;
        try {
            constructor = parent.getDeclaredConstructor(EntityType.class, Level.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(parent.getName() + " has no (EntityType, Level) constructor");
        }
        if (Modifier.isPrivate(constructor.getModifiers())) {
            throw new IllegalStateException(parent.getName() + " has a private (EntityType, Level) constructor");
        }
        for (Method method : ExtraServantTemplate.class.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) || Modifier.isPrivate(method.getModifiers()) || method.isSynthetic()) {
                continue;
            }
            Method inherited = findMethod(parent, method.getName(), method.getParameterTypes());
            if (inherited != null && Modifier.isFinal(inherited.getModifiers())) {
                throw new IllegalStateException(parent.getName() + " makes " + method.getName() + " final");
            }
        }
        if (!definition.replaceBrain() && usesBrain(parent)) {
            throw new IllegalStateException(parent.getName() + " is driven by a Brain, not by goals: set \"replace_brain\": true");
        }
    }

    @Nullable
    private static Method findMethod(Class<?> type, String name, Class<?>[] parameters) {
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            try {
                return current.getDeclaredMethod(name, parameters);
            } catch (NoSuchMethodException ignored) {
                // Look further up.
            }
        }
        return null;
    }

    /** Whether the mob builds its own Brain, as piglins, villagers and most newer mobs do, instead of using goals. */
    private static boolean usesBrain(Class<?> type) {
        for (Class<?> current = type; current != null && current != LivingEntity.class; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals("makeBrain") || method.getName().equals("brainProvider")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The template's bytes with a new name and a new superclass. References to the template become references to the
     * copy; the superclass, the super constructor call and the super method calls move from PathfinderMob to the mob.
     */
    private static byte[] copyTemplate(String name, String superclass) throws IOException {
        byte[] template;
        try (InputStream in = ExtraServantClasses.class.getResourceAsStream("ExtraServantTemplate.class")) {
            if (in == null) {
                throw new IOException("ExtraServantTemplate.class is missing");
            }
            template = in.readAllBytes();
        }
        ClassWriter writer = new ClassWriter(0);
        ClassVisitor reparent = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public void visit(int version, int access, String className, String signature, String superName, String[] interfaces) {
                super.visit(version, access & ~Opcodes.ACC_ABSTRACT, className, null, superclass, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, methodName, descriptor, signature, exceptions)) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String method, String desc, boolean isInterface) {
                        super.visitMethodInsn(opcode, opcode == Opcodes.INVOKESPECIAL && owner.equals(TEMPLATE_SUPER) ? superclass : owner, method, desc, isInterface);
                    }
                };
            }
        };
        new ClassReader(template).accept(new ClassRemapper(reparent, new SimpleRemapper(TEMPLATE, name)), 0);
        return writer.toByteArray();
    }
}
