package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.objects.Enemy;

public class EnemySprinter extends Enemy {

    private final static int HEALTH = 500;
    private final static float MOVEMENT_SPEED = 2.5f;

    private float mAngle;
    private Sprite mSprite;

    public EnemySprinter() {
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
    }

    @Override
    public void init() {
        super.init();

        mSprite = Sprite.fromResources(this, R.drawable.sprinter_enemy);
        mSprite.calcMatrix(0.9f);
        mSprite.setLayer(Layers.ENEMY);
        mGame.add(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);
    }

    @Override
    public void tick() {
        super.tick();

        if (hasWayPoint()) {
            mAngle = getDirectionTo(getWayPoint()).angle();
        }
    }
}
