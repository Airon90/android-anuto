package ch.logixisland.anuto.view.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.control.TowerSelector;
import ch.logixisland.anuto.engine.logic.GameEngine;

public class GameActivity extends Activity {

    private final GameEngine mGameEngine;
    private final TowerSelector mTowerSelector;

    private GameView view_tower_defense;

    public GameActivity() {
        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mGameEngine = factory.getGameEngine();
        mTowerSelector = factory.getTowerSelector();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        view_tower_defense = (GameView) findViewById(R.id.view_tower_defense);
    }

    @Override
    public void onResume() {
        super.onStart();
        mGameEngine.start();
    }

    @Override
    public void onPause() {
        super.onStop();
        mGameEngine.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view_tower_defense.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mTowerSelector.selectTower(null);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
