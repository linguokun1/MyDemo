package com.sponia.opyfunctioindemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sponia.opyfunctioindemo.R;
import com.sponia.opyfunctioindemo.customedview.MySurfaceView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SurfaceActivity extends AppCompatActivity {

    @Bind(R.id.btn_clear)
    Button mBtnClear;
    @Bind(R.id.surface_view)
    MySurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.clear();
            }
        });
    }
}
