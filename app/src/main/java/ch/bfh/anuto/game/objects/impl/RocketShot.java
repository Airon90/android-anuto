package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.TargetedShot;
import ch.bfh.anuto.util.math.Vector2;

public class RocketShot extends TargetedShot {

    private final static float DAMAGE = 200f;
    private final static float MOVEMENT_SPEED = 3f;

    private Sprite mSprite;
    private float mAngle = 0f;

    public RocketShot() {
        mSpeed = MOVEMENT_SPEED;
    }

    public RocketShot(Vector2 position, Enemy target) {
        this();

        setPosition(position);
        setTarget(target);

        mAngle = getDirectionTo(mTarget).angle();
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);
    }

    @Override
    public void init() {
        super.init();

        mSprite = Sprite.fromResources(this, R.drawable.rocket_shot, 4);
        mSprite.calcMatrix(1f);
        mSprite.setLayer(Layers.SHOT);
        mGame.add(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        mDirection = getDirectionTo(mTarget);
        mAngle = mDirection.angle();

        super.tick();
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = (Enemy)mGame.getGameObjects(Enemy.TYPE_ID)
                .min(GameObject.distanceTo(mPosition));

        if (closest == null) {
            mGame.remove(this);
        } else {
            setTarget(closest);
        }
    }

    @Override
    protected void onTargetReached() {
        mTarget.damage(DAMAGE);
        mGame.remove(this);
    }
}
