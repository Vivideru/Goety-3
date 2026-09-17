package com.Polarice3.Goety.api.items.magic;

import com.Polarice3.Goety.api.magic.ISpell;

/**
 * An item bound to a single spell; items sharing a spell also share its cooldown.
 */
public interface ISpellHolder {
    ISpell getSpell();
}
