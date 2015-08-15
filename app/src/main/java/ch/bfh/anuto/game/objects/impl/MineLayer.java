package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.data.Path;
import ch.bfh.anuto.game.objects.DrawObject;
import ch.bfh.anuto.game.objects.GameObject;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.game.objects.Tower;
import ch.bfh.anuto.util.math.Intersections;
import ch.bfh.anuto.util.math.MathUtils;
import ch.bfh.anuto.util.math.Vector2;

public class MineLayer extends Tower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 1.5f;
    private final static float RANGE = 3.5f;
    private final static int MAX_MINE_COUNT = 3;

    private final static float ANIMATION_SPEED = 2.0f;

    private class Section {
        Vector2 p1;
        Vector2 p2;
        float len;
    }

    private float mAngle;
    private boolean mShooting;
    private float mSectionTotalLength;
    private final List<Mine> mMines = new ArrayList<>();
    private final List<Section> mSections = new ArrayList<>();

    private Sprite mSprite;
    private Sprite.Animator mAnimator;

    private final Listener mMineListener = new Listener() {
        @Override
        public void onObjectAdded(GameObject obj) {

        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            mMines.remove(obj);
            obj.removeListener(this);
        }
    };

    public MineLayer() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;

        mAngle = 90f;

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.catapult, 6);
        mSprite.setListener(this);
        mSprite.setMatrix(1f, 1f, null, null);
        mSprite.setLayer(Layers.TOWER);

        mAnimator = new Sprite.Animator();
        mAnimator.setSequence(mSprite.sequenceForwardBackward());
        mAnimator.setSpeed(ANIMATION_SPEED);
        mSprite.setAnimator(mAnimator);
    }

    @Override
    public void init() {
        super.init();

        mAngle = mGame.getRandom(360f);
        mGame.add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);

        for (Mine m : mMines) {
            m.removeListener(mMineListener);
        }
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (mReloaded && mMines.size() < MAX_MINE_COUNT && mSections.size() > 0) {
            mShooting = true;
            mReloaded = false;
        }

        if (mShooting) {
            mSprite.animate();

            if (mAnimator.getPosition() == 5) {
                Mine m = new Mine(mPosition, getTarget());
                m.addListener(mMineListener);
                mMines.add(m);
                mGame.add(m);

                mShooting = false;
            }
        }

        if (mAnimator.getPosition() != 0) {
            mSprite.animate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            determineSections();
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        mSprite.draw(canvas);
    }

    private void determineSections() {
        mSections.clear();
        mSectionTotalLength = 0f;

        float r2 = MathUtils.square(mRange);

        for (Path p : GameManager.getInstance().getLevel().getPaths()) {
            for (int i = 1; i < p.getWayPoints().size(); i++) {
                Vector2 p1 = p.getWayPoints().get(i - 1).copy().sub(mPosition);
                Vector2 p2 = p.getWayPoints().get(i).copy().sub(mPosition);

                boolean p1in = p1.len2() <= r2;
                boolean p2in = p2.len2() <= r2;

                Vector2[] is = Intersections.lineCircle(p1, p2, mRange);

                Section s = new Section();

                if (p1in && p2in) {
                    s.p1 = p1.add(mPosition);
                    s.p2 = p2.add(mPosition);
                } else if (!p1in && !p2in) {
                    if (is == null) {
                        continue;
                    }

                    float a1 = Vector2.fromTo(is[0], p1).angle();
                    float a2 = Vector2.fromTo(is[0], p2).angle();

                    if (MathUtils.equals(a1, a2, 10f)) {
                        continue;
                    }

                    s.p1 = is[0].add(mPosition);
                    s.p2 = is[1].add(mPosition);
                }
                else {
                    float angle = Vector2.fromTo(p1, p2).angle();

                    if (p1in) {
                        if (MathUtils.equals(angle, Vector2.fromTo(p1, is[0]).angle(), 10f)) {
                            s.p2 = is[0].add(mPosition);
                        } else {
                            s.p2 = is[1].add(mPosition);
                        }

                        s.p1 = p1.add(mPosition);
                    } else {
                        if (MathUtils.equals(angle, Vector2.fromTo(is[0], p2).angle(), 10f)) {
                            s.p1 = is[0].add(mPosition);
                        } else {
                            s.p1 = is[1].add(mPosition);
                        }

                        s.p2 = p2.add(mPosition);
                    }
                }

                s.len = Vector2.fromTo(s.p1, s.p2).len();
                mSectionTotalLength += s.len;
                mSections.add(s);
            }
        }
    }

    private Vector2 getTarget() {
        float sectionDist = mGame.getRandom(mSectionTotalLength);

        for (Section s : mSections) {
            if (sectionDist > s.len) {
                sectionDist -= s.len;
            } else {
                Vector2 d = Vector2.fromTo(s.p1, s.p2);
                return d.norm().mul(sectionDist).add(s.p1);
            }
        }

        return null;
    }

    private void debugSections() {
        mGame.add(new DrawObject() {
            Paint mPen;

            @Override
            public int getLayer() {
                return Layers.TOWER_RANGE;
            }

            @Override
            public void draw(Canvas canvas) {
                if (mPen == null) {
                    mPen = new Paint();
                    mPen.setStyle(Paint.Style.STROKE);
                    mPen.setStrokeWidth(0.05f);
                    mPen.setColor(Color.RED);
                    mPen.setAlpha(128);
                }

                canvas.drawCircle(mPosition.x, mPosition.y, mRange, mPen);

                for (Section s : mSections) {
                    canvas.drawLine(s.p1.x, s.p1.y, s.p2.x, s.p2.y, mPen);
                }
            }
        });
    }
}
