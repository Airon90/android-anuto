package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;

public class RocketTower extends Tower {

    private final static int RELOAD_TIME = 20;
    private final static float RANGE = 5f;

    private float mAngle;

    public RocketTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public RocketTower(PointF position) {
        this();
        setPosition(position);
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasTarget()) {
            nextTarget();
        }

        if (hasTarget()) {
            if (isReloaded()) {
                shoot(new BasicShot(this, mTarget));
            }

            mAngle = getAngleToTarget();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.rotate(mAngle);
        mSprite.draw(canvas);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.rocket_tower);
        mSprite.getMatrix().postScale(2f, 2f);
        mSprite.getMatrix().postTranslate(0f, -0.45f);
    }

    @Override
    protected void onTargetLost() {
        nextTarget();
    }
}