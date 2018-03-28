package com.ruiduoyi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ruiduoyi.R;

public class PreViewDialogActivity extends BaseDialogActivity implements View.OnClickListener {
    Button cancelBtn;
    TextView titleText;
    TextView msgText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view_dialog);
        initView();
    }

    public void initView(){
        cancelBtn=(Button)findViewById(R.id.cancel_btn);
        titleText=(TextView)findViewById(R.id.title);
        msgText=(TextView)findViewById(R.id.msg);
        cancelBtn.setOnClickListener(this);
        titleText.setText(getIntent().getStringExtra("title"));
        msgText.setText(getIntent().getStringExtra("msg"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_btn:
                finish();
                break;
        }
    }
}
