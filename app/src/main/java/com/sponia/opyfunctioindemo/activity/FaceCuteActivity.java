package com.sponia.opyfunctioindemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sponia.opyfunctioindemo.R;
import com.sponia.opyfunctioindemo.customedview.FaceCuteView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FaceCuteActivity extends AppCompatActivity {
    @Bind(R.id.btn_clear)
    Button mBtnClear;
    @Bind(R.id.surface_view)
    FaceCuteView mSurfaceView;
    @Bind(R.id.iv_hair1)
    ImageView mIvHair1;
    @Bind(R.id.iv_hair2)
    ImageView mIvHair2;
    @Bind(R.id.iv_face1)
    ImageView mIvFace2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_cute);
        ButterKnife.bind(this);
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initListener() {
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.clear();
//                mSurfaceView.drawFace();
            }
        });

        mIvHair1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mIvHair1.
                mSurfaceView.drawHair(mIvHair1.getDrawable());
            }
        });

        mIvHair2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.drawHair(mIvHair2.getDrawable());
            }
        });
        mIvFace2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.drawFace(mIvFace2.getDrawable());
            }
        });
    }
}
