package com.sponia.opyfunctioindemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sponia.opyfunctioindemo.R;
import com.sponia.opyfunctioindemo.customedview.LoveLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StarActivity extends AppCompatActivity {
    @Bind(R.id.iv_icon)
    ImageView mIvIcon;
    @Bind(R.id.zan)
    LoveLayout mZan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        ButterKnife.bind(this);

        mZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //出现一个星星图片,并且开始动画
                mZan.addStar();
            }
        });
    }
}
