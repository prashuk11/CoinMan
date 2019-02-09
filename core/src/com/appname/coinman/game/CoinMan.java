package com.appname.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.soap.Text;

import sun.rmi.runtime.Log;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	int gameState = 0;

	Texture background;
	Texture[] man;
	Texture coin;
	Texture bomb;
	Texture dizzy;

	int slowDownSpeed;
	int manState;
    int coinCount;
    int bombCount;
    int heightScreen;
    int score;

	float gravity;
	float velocity;
	float manY;

	ArrayList<Integer> coinXs;
	ArrayList<Integer> coinYs;
    ArrayList<Integer> bombXs;
    ArrayList<Integer> bombYs;

    ArrayList<Rectangle> coinRectangle;
    ArrayList<Rectangle> bombRectangle;
    Rectangle manRectangle;

	Random rand;
	BitmapFont scoreFont;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");

		slowDownSpeed = 0;
		manState = 0;
		score = 0;
		gameState = 0;
        manY = Gdx.graphics.getHeight()/2;
        heightScreen = Gdx.graphics.getHeight();

		gravity = -0.2f;
		velocity = -0.5f;

		rand = new Random();
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(10);

		coinXs = new ArrayList<Integer>();
		coinYs = new ArrayList<Integer>();
        bombXs = new ArrayList<Integer>();
        bombYs = new ArrayList<Integer>();

        coinRectangle = new ArrayList<Rectangle>();
        bombRectangle = new ArrayList<Rectangle>();

	}

	public void makeCoin() {

	    coinXs.add(Gdx.graphics.getWidth());
	    coinYs.add((int)(rand.nextFloat() * Gdx.graphics.getHeight()));

    }

    public void makeBomb() {

        bombXs.add(Gdx.graphics.getWidth());
        bombYs.add((int)(rand.nextFloat() * Gdx.graphics.getHeight()));

    }

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 0) {
		    // Waiting to start

            batch.draw(man[0], Gdx.graphics.getWidth()/2 - man[0].getWidth()/2, Gdx.graphics.getHeight()/2);

            if(Gdx.input.justTouched() == true) {

                gameState = 1;

            }

        } else if(gameState == 1) {
		    // Game is Live

            if(bombCount < 250) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }

            for(int i = 0 ; i < bombXs.size(); i++) {
                bombXs.set(i, bombXs.get(i) - 6);
                batch.draw(bomb , bombXs.get(i), bombYs.get(i));
            }

            bombRectangle.clear();
            for(int i = 0 ; i < bombXs.size(); i++) {
                bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth()/2, bomb.getHeight()/2));
            }

            // COINS
            if(coinCount < 125) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }

            for(int i = 0 ; i < coinXs.size(); i++) {
                coinXs.set(i, coinXs.get(i) - 4);
                batch.draw(coin , coinXs.get(i), coinYs.get(i));
            }

            coinRectangle.clear();
            for(int i = 0 ; i < coinXs.size(); i++) {
                coinRectangle.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth()/2, coin.getHeight()/2));
            }


            if(Gdx.input.justTouched() == true) {

                velocity = 10;

            }

            if(slowDownSpeed == 9) {
                slowDownSpeed = 0;

                manState++;

                if(manState == 4) {
                    manState = 0;
                }

            } else {
                slowDownSpeed++;
            }

            velocity = velocity + gravity;
            manY = manY + velocity;

            if(manY >= heightScreen - man[manState].getHeight()) {

                manY = heightScreen - man[manState].getHeight();
                velocity = -0.5f;

            }

            if(manY <= 0) {
                manY = 0;
            }

            batch.draw(man[manState], Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY);

            manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY, (man[manState].getWidth()*19)/20, (man[manState].getHeight()*19)/20);

            for(int i = 0 ; i < coinXs.size(); i++) {
                if(Intersector.overlaps(manRectangle, coinRectangle.get(i))) {

                    score++;
                    coinRectangle.remove(i);
                    coinXs.remove(i);
                    coinYs.remove(i);
                    break; // We Dont want to access an Index out of Bounds
                }
            }

            for(int i = 0 ; i < bombXs.size(); i++) {
                if(Intersector.overlaps(manRectangle, bombRectangle.get(i))){

                    coinXs.clear();
                    coinYs.clear();
                    coinRectangle.clear();
                    bombXs.clear();
                    bombYs.clear();
                    bombRectangle.clear();

                    slowDownSpeed = 0;
                    manState = 0;
                    score = 0;
                    gameState = 0;
                    heightScreen = Gdx.graphics.getHeight();

                    gravity = -0.2f;
                    velocity = -0.5f;

                    gameState = 2;
                }
            }

            scoreFont.draw(batch, String.valueOf(score), 100, 200);

        } else if(gameState == 2) {
		    // Game has Ended
            batch.draw(dizzy, Gdx.graphics.getWidth()/2 - dizzy.getWidth()/2, manY);

            if(Gdx.input.justTouched() == true) {
                manY = Gdx.graphics.getHeight()/2;
                gameState = 1;

            }
        }

		//BOMBS

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
