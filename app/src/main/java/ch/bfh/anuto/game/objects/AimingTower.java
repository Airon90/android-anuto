package ch.bfh.anuto.game.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.bfh.anuto.game.GameObject;

public abstract class AimingTower extends Tower implements GameObject.Listener {

    public enum Strategy {
        Closest,
        Weakest,
        Strongest
    }

    /*
    ------ Members ------
     */

    protected Enemy mTarget = null;
    protected Strategy mStrategy = Strategy.Closest;
    protected boolean mLockOnTarget = true;

    /*
    ------ Methods ------
     */

    @Override
    public void tick() {
        super.tick();

        if (hasTarget() && getDistanceTo(mTarget) > mRange) {
            onTargetLost();
        }

        if (!hasTarget() || !mLockOnTarget) {
            nextTarget();
        }
    }


    protected void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(this);
        }

        mTarget = target;

        if (mTarget != null) {
            mTarget.addListener(this);
        }
    }

    public boolean hasTarget() {
        return mTarget != null;
    }

    protected void nextTarget() {
        switch (mStrategy) {
            case Closest:
                setTarget((Enemy)GameObject.closest(mGame.getObjects(Enemy.TYPE_ID), mPosition));
                break;

            case Strongest:
                setTarget(Enemy.strongest(getEnemiesInRange()));
                break;

            case Weakest:
                setTarget(Enemy.weakest(getEnemiesInRange()));
                break;
        }
     }

    protected void onTargetLost() {
        setTarget(null);
    }


    @Override
    protected void onRemove() {
        setTarget(null);
    }

    /*
    ------ GameObject Listener ------
     */

    @Override
    public void onAddObject(GameObject obj) {
    }

    @Override
    public void onRemoveObject(GameObject obj) {
        onTargetLost();
    }
}