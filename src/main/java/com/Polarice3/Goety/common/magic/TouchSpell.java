package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.ITouchSpell;

public abstract class TouchSpell extends Spell implements ITouchSpell {

    public int defaultCastDuration() {
        return 0;
    }

}
