package tdp.siu;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class AlphaBackGroundActivity extends AppCompatActivity {
    int ALPHA = 140;

    public void setAlphaBackGround(){
        View backgroundImage = findViewById(R.id.background_profile);
        Drawable background = backgroundImage.getBackground();
        background.setAlpha(ALPHA);
    }


}
