package com.boontaran.supercat.level.controls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ControlPad extends Group {
	//images
	private Image idle;
	private Image right;
	private Image left;
	
	//states
	private static final int IDLE = 0;
	private static final int RIGHT = 1;
	private static final int LEFT = 2;
	private int direction;

	private boolean isTouched;
	
	
	public ControlPad(TextureRegion normal, TextureRegion right,TextureRegion left) {
        setTransform(false);

		//create 3 images of state

		//no touch
		idle = new Image(normal);
		addActor(idle);
		setSize(idle.getWidth(), idle.getHeight());

		//right
		this.right = new Image(right);
		addActor(this.right);

		//left
		this.left = new Image(left);
		addActor(this.left);
					
		setDir(IDLE);

		//monitor touching
		addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				
				isTouched = true;
				handleTouch(x,y);
				
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (isTouched) {
					handleTouch(x, y);
					
				}
				super.touchDragged(event, x, y, pointer);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				
				isTouched = false;
				setDir(IDLE);
				
				super.touchUp(event, x, y, pointer, button);
			}
			
		});
		
	}
	
	
	public boolean isRight() {
		return direction == RIGHT;
	}
	public boolean isLeft() {
		return direction == LEFT;
	}

	//where is the touch point
	private void handleTouch(float x,float y){
		//is it on left or right side
		if(x > getWidth()/2) {
			setDir(RIGHT);
		} else {
			setDir(LEFT);
		}
	}

	//set display based on direction
	private void setDir(int dir) {
		idle.setVisible (false);
		right.setVisible(false);
		left.setVisible(false);
		
		if(dir==IDLE) idle.setVisible(true);
		if(dir==RIGHT) right.setVisible(true);
		if(dir==LEFT) left.setVisible(true);
		
		direction = dir;
		
	}

}
