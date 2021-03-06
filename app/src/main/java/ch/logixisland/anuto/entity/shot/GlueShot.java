package ch.logixisland.anuto.entity.shot;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.effect.GlueEffect;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class GlueShot extends Shot {

    public final static float MOVEMENT_SPEED = 4.0f;
    private final static float ANIMATION_SPEED = 1.0f;

    private class StaticData {
        public SpriteTemplate mSpriteTemplate;
    }

    private float mIntensity;
    private float mDuration;
    private Vector2 mTarget;

    private AnimatedSprite mSprite;

    public GlueShot(Entity origin, Vector2 position, Vector2 target, float intensity, float duration) {
        super(origin);
        setPosition(position);
        mTarget = new Vector2(target);

        setSpeed(MOVEMENT_SPEED);
        setDirection(getDirectionTo(target));

        mIntensity = intensity;
        mDuration = duration;

        StaticData s = (StaticData) getStaticData();

        mSprite = getSpriteFactory().createAnimated(Layers.SHOT, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setSequenceForward();
        mSprite.setFrequency(ANIMATION_SPEED);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.glue_shot, 6);
        s.mSpriteTemplate.setMatrix(0.33f, 0.33f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSprite);
    }

    @Override
    public void tick() {
        super.tick();

        mSprite.tick();

        if (getDistanceTo(mTarget) < getSpeed() / GameEngine.TARGET_FRAME_RATE) {
            getGameEngine().add(new GlueEffect(getOrigin(), mTarget, mIntensity, mDuration));
            this.remove();
        }
    }
}
