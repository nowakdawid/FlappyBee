package it.works.flappybee;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;

    private Flappy flappy;

    private Array<Flower> flowers = new Array<Flower>();

    private static final float GAP_BETWEEN_FLOWERS = 200F;

    private int score = 0;

    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

    private Texture background;

    private Texture flowerBottom;
    private Texture flowerTop;

    private Texture flappyTexture;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
        background = new Texture(Gdx.files.internal("bg.png"));
        flowerBottom = new Texture(Gdx.files.internal("flowerBottom.png"));
        flowerTop = new Texture(Gdx.files.internal("flowerTop.png"));
        flappyTexture = new Texture(Gdx.files.internal("bee.png"));
        flappy = new Flappy(flappyTexture);
        flappy.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        update(delta);
        clearScreen();
        draw();
//        drawDebug();
    }

    private void update(float delta) {
        updateFlappy(delta);
        updateFlowers(delta);
        updateScore();
        if (checkForCollision()) {
            restart();
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void blockFlappyLeavingTheWorld() {
        flappy.setPosition(flappy.getX(), MathUtils.clamp(flappy.getY(), 0, WORLD_HEIGHT));
    }

    private void updateFlappy(float delta) {
        flappy.update();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) flappy.flyUp();
        blockFlappyLeavingTheWorld();
    }

    private void createNewFlower() {
        Flower newFlower = new Flower(flowerBottom, flowerTop);
        newFlower.setPosition(WORLD_WIDTH + Flower.WIDTH);
        flowers.add(newFlower);
    }

    private void checkIfNewFlowerIsNeeded() {
        if (flowers.size == 0) {
            createNewFlower();
        } else {
            Flower flower = flowers.peek();
            if (flower.getX() < WORLD_WIDTH - GAP_BETWEEN_FLOWERS) {
                createNewFlower();
            }
        }
    }

    private void removeFlowersIfPassed() {
        if (flowers.size > 0) {
            Flower firstFlower = flowers.first();
            if (firstFlower.getX() < -Flower.WIDTH) {
                flowers.removeValue(firstFlower, true);
            }
        }
    }

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.end();
        batch.begin();
        batch.draw(background, 0, 0);
        drawFlowers();
        flappy.draw(batch);
        drawScore();
        batch.end();
    }

    private void updateFlowers(float delta) {

        for (Flower flower : flowers) {
            flower.update(delta);
        }

        checkIfNewFlowerIsNeeded();
        removeFlowersIfPassed();
    }

    private void drawDebug() {
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Flower flower : flowers) {
            flower.drawDebug(shapeRenderer);
        }

        flappy.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }

    private boolean checkForCollision() {
        for (Flower flower : flowers) {
            if (flower.isFlappeeColliding(flappy)) {
                return true;
            }
        }
        return false;
    }

    private void restart() {
        flappy.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        flowers.clear();
        score = 0;
    }

    private void updateScore() {
        Flower flower = flowers.first();
        if (flower.getX() < flappy.getX() && !flower.isPointClaimed()) {
            flower.markPointClaimed();
            score++;
        }
    }

    private void drawScore() {
        String scoreAsString = Integer.toString(score);
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString, (viewport.getWorldWidth() / 2),
                (4 * viewport.getWorldHeight() / 5) - glyphLayout.height / 2);
    }

    private void drawFlowers() {
        for (Flower flower : flowers) {
            flower.draw(batch);
        }
    }

}
