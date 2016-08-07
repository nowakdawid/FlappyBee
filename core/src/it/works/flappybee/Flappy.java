package it.works.flappybee;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

public class Flappy {

    private static final float COLLISION_RADIUS = 24f;
    private final Circle collisionCircle;

    private float x = 0;
    private float y = 0;

    private static final float DIVE_ACCEL = 0.3F;
    private static final float FLY_ACCEL = 5F;
    private float ySpeed = 0;

    private TextureRegion flappyTexture;

    private static final int TILE_WIDTH = 118;
    private static final int TILE_HEIGHT = 118;

    public Flappy(Texture flappyTexture) {
        this.flappyTexture = new TextureRegion(flappyTexture).split(TILE_WIDTH, TILE_HEIGHT)[0][0];
        collisionCircle = new Circle(x, y, COLLISION_RADIUS);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateCollisionCircle();
    }

    private void updateCollisionCircle() {
        collisionCircle.setX(x);
        collisionCircle.setY(y);
    }

    public void update() {
        ySpeed -= DIVE_ACCEL;
        setPosition(x, y + ySpeed);
    }

    public void flyUp() {
        ySpeed = FLY_ACCEL;
        setPosition(x, y + ySpeed);
    }

    public void draw(SpriteBatch batch) {
        float textureX = collisionCircle.x - flappyTexture.getRegionWidth() / 2;
        float textureY = collisionCircle.y - flappyTexture.getRegionHeight() / 2;
        batch.draw(flappyTexture, textureX, textureY);
    }

    //Getters and Setters
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }
}
