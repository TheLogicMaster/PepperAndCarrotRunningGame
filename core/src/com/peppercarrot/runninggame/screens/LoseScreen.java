package com.peppercarrot.runninggame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.peppercarrot.runninggame.utils.Assets;
import com.peppercarrot.runninggame.utils.Constants;

/**
 * 
 * @author momsen
 *
 */
public class LoseScreen extends ScreenAdapter {
	Stage stage;
	boolean goToWorldMap = false;
	boolean goToStartScreen = false;

	public LoseScreen() {
		stage = new Stage(DefaultScreenConfiguration.getInstance().getViewport());
		// set up stage
		final Table table = new Table(Assets.I.skin);
		table.setFillParent(true);
		table.setWidth(Constants.VIRTUAL_WIDTH);
		table.setHeight(Constants.VIRTUAL_HEIGHT);
		final TextButton tryAgainBtn = new TextButton("Try again", Assets.I.skin, "default");
		tryAgainBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				goToWorldMap = true;
				event.cancel();
				return true;
			}
		});
		final TextButton exitBtn = new TextButton("Exit", Assets.I.skin, "default");
		exitBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				goToStartScreen = true;
				event.cancel();
				return true;
			}
		});
		table.add(exitBtn).bottom().padBottom(60);
		table.add(tryAgainBtn).bottom().padLeft(70).padBottom(60);
		table.bottom();
		stage.addActor(table);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {

		final Batch batch = DefaultScreenConfiguration.getInstance().getBatch();
		batch.begin();
		batch.setColor(1, 1, 1, 1);
		// TODO: render some background image
		batch.end();

		stage.act(delta);
		stage.draw();

		if (goToStartScreen || goToWorldMap) {
			switchScreen(0.25f);
		}

		if (Gdx.input.isKeyJustPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	/**
	 * Fade out animation that takes fadeOutTime long.
	 * 
	 * @param fadeOutTime
	 */
	public void switchScreen(float fadeOutTime) {
		stage.getRoot().getColor().a = 1;
		final SequenceAction sequenceAction = new SequenceAction();
		sequenceAction.addAction(Actions.fadeOut(fadeOutTime));
		sequenceAction.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				if (goToWorldMap) {
					ScreenSwitch.getInstance().setWorldScreen();
				}
				if (goToStartScreen) {
					ScreenSwitch.getInstance().setStartScreen();
				}
			}
		}));
		stage.getRoot().addAction(sequenceAction);
		/*
		 * backgroundImage.getColor().a = 1; SequenceAction sequenceAction2 =
		 * new SequenceAction(); sequenceAction2.addAction(
		 * Actions.fadeOut(fadeOutTime) );
		 * backgroundImage.addAction(sequenceAction2);
		 */
	}
}
