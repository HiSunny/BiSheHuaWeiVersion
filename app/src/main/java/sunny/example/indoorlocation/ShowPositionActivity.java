package sunny.example.indoorlocation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import utils.CoordPoint;
import utils.ObtainStepData;

import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;

public class ShowPositionActivity extends BaseActivity{

    private Button mLeft;
    private Button mRight;
    private Button mBottom;
    private Button mOffLineLearnButton;
    private Button mShowPositionButton;
    private Button mSettingsButton;
    private BottomButtonClickListener mBottomButtonClickListener;

    static int[] newCoords = new int[2];
    static int[] curTouchCoords = {0, 0};
    static float[] myCoords = new float[2];
    // Time variables
    final long updateItemMilliTime = 50;
    TimerTask mTimetask;
    private Bitmap mBackgroundMap;
    private Bitmap mMark;
    private Bitmap mResultBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private ImageView mIndoorMap;
    private TextView mSetpCount;
    private TextView mStepLength;
    private TextView mStepDegree;
    private TextView mStepCoordinate;
    private ObtainStepData mObtainStepData;
    private Button mStartButton;
    private Button mResetButton;
    private Button mStopButton;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showposition);


        initBottomButton();

        mIndoorMap = (ImageView)findViewById(R.id.image_map);

        //使高度充满 day3 12.8
        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        ViewGroup.LayoutParams lp = mIndoorMap.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mIndoorMap.setLayoutParams(lp);

        mIndoorMap.setMaxWidth(width);
        mIndoorMap.setMaxHeight(width * 5);
        mIndoorMap.setMaxWidth(height);
        mIndoorMap.setMaxHeight(height * 5);

        //sMapId=0实验室
        if(SettingActivity.sMapId ==0) {
            mBackgroundMap = BitmapFactory.decodeResource(getResources(), R.drawable.lab);
        } else {
            mBackgroundMap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        }
        mMark = BitmapFactory.decodeResource(getResources(), R.drawable.red_mark);

        mResultBitmap = Bitmap.createBitmap(mBackgroundMap.getWidth(), mBackgroundMap.getHeight(), mBackgroundMap.getConfig());

        mCanvas = new Canvas(mResultBitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);

    /*void android.graphics.Canvas.drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint)
	Draw the bitmap using the specified matrix.
     */
        mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
//        mCanvas.drawBitmap(mMark, 150, 1480, mPaint);//暂定测试用起始点
        mIndoorMap.setImageBitmap(mResultBitmap);


        mSetpCount = (TextView)findViewById(R.id.text_view_step_count);
        mStepLength = (TextView) findViewById(R.id.text_view_step_length);
        mStepDegree = (TextView) findViewById(R.id.text_view_step_degree);
        mStepCoordinate = (TextView) findViewById(R.id.text_view_step_coordinate);

        mLeft = (Button)findViewById(R.id.pic_left);
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeftPopupWindow();
            }
        });

        mRight = (Button)findViewById(R.id.pic_right);
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRightPopupWindow();
            }
        });

        mBottom = (Button)findViewById(R.id.pic_bottom);
        mBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomPopupWindow();
            }
        });


        //utils.ObtainStepData.ObtainStepData(Context context, TextView stepCount,
        //TextView stepLength,TextView degree, TextView coordinate)
        //获取步数、步长、角度、坐标
        mObtainStepData = new ObtainStepData(this, mSetpCount, mStepLength, mStepDegree, mStepCoordinate);

        //特殊处理－－每次进入定位页面,默认调用一次ｒｅｓｅｔ
        mObtainStepData.correctStep();
        //清除上次测量的坐标
        mObtainStepData.clearPoints();
        //初始化行人的初始坐标
        mObtainStepData.initPoints();
        //步数、步长、角度等TextView都不显示
        mObtainStepData.stepViewGone();
        //开始测量
        mStartButton = (Button) findViewById(R.id.button_start);
        /**
         * obtainStepSetting
         * initPoints
         * obtainStep
         */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //让步数、步长、角度等TextView都visible
                mObtainStepData.obtainStepSetting();
                mObtainStepData.initPoints();
                mObtainStepData.obtainStep();
            }
        });
        mResetButton = (Button) findViewById(R.id.button_reset);
        /**
         * correctStep
         * clearPoints
         * initPoints
         * stepViewgone
         */
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObtainStepData.correctStep();
                mObtainStepData.clearPoints();
                mObtainStepData.initPoints();
                mObtainStepData.stepViewGone();
            }
        });
        mStopButton = (Button) findViewById(R.id.button_stop);
        /**
         * stop
         */
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObtainStepData.stopStep();
            }
        });


        stepShowTaskSchedule(updateItemMilliTime);
    }

    private void showLeftPopupWindow(){

        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_left, null);



        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);

        // 必须要给调用这个方法，否则点击popWindow以外部分，popWindow不会消失
        // window.setBackgroundDrawable(new BitmapDrawable());

        // 实例化一个ColorDrawable颜色为半透明0xb0000000
        ColorDrawable dw = new ColorDrawable(0x00000000);
        window.setBackgroundDrawable(dw);

        int[] location = new int[2];
        mLeft.getLocationOnScreen(location);
        window.showAtLocation(mLeft, Gravity.NO_GRAVITY, location[0]+mLeft.getWidth(), location[1]);

        //window.showAsDropDown(view);
        // popWindow消失监听方法
        window.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                //System.out.println("popWindow消失");
            }
        });
    }

    private void showRightPopupWindow(){

        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_right, null);



        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);

        // 必须要给调用这个方法，否则点击popWindow以外部分，popWindow不会消失
        // window.setBackgroundDrawable(new BitmapDrawable());

        // 实例化一个ColorDrawable颜色为半透明0xb0000000
        ColorDrawable dw = new ColorDrawable(0x00000000);
        window.setBackgroundDrawable(dw);

        int[] location = new int[2];
        mRight.getLocationOnScreen(location);
        window.showAtLocation(mRight, Gravity.NO_GRAVITY, location[0]-mRight.getWidth(), location[1]);

        //window.showAsDropDown(view);
        // popWindow消失监听方法
        window.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                //System.out.println("popWindow消失");
            }
        });
    }

    private void showBottomPopupWindow(){

        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_bottom, null);



        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);

        // 必须要给调用这个方法，否则点击popWindow以外部分，popWindow不会消失
        // window.setBackgroundDrawable(new BitmapDrawable());

        // 实例化一个ColorDrawable颜色为半透明0xb0000000
        ColorDrawable dw = new ColorDrawable(0x00000000);
        window.setBackgroundDrawable(dw);

        int[] location = new int[2];
        mBottom.getLocationOnScreen(location);
        window.showAtLocation( mBottom, Gravity.NO_GRAVITY, location[0], location[1]-mBottom.getHeight());

        //window.showAsDropDown(view);
        // popWindow消失监听方法
        window.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                //System.out.println("popWindow消失");
            }
        });
    }




    public void stepShowTaskSchedule(long milliTime) {
        mTimer = new Timer();
        mTimetask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCanvas.drawBitmap(mBackgroundMap, new Matrix(), null);
                        //获取移动一步的坐标
                        myCoords = ObtainStepData.getCurCoordsOfStep();
                        //public int[] convertTouchCoordinates(float[] coors)适应不同屏幕
                        //转换后的初始坐标需要达到190，1040
                        curTouchCoords = convertTouchCoordinates(myCoords);
                        drawTrajectory();
                        //////////不断更新mMark的位置
                        mCanvas.drawBitmap(mMark,curTouchCoords[0],curTouchCoords[1], mPaint);
                        mIndoorMap.setImageBitmap(mResultBitmap);
                    }
                });//190,1040 curTouchCoords[0] curTouchCoords[1]
            }
        };
        mTimer.schedule(mTimetask, 0, milliTime);
    }

    //画轨迹，
    private void drawTrajectory() {
        /*
         * draw the line of trajectory.
		 */
        //获取坐标 ObtainStepData.getPoints()
        ArrayList<CoordPoint> tmpPoints = ObtainStepData.getPoints();
        for (int i = 0; i < tmpPoints.size() - 1; i++) {
        	/*public class CoordPoint {

    public float px;
    public float py;

    public CoordPoint(float px, float py) {
        this.px = px;
        this.py = py;
    }
}*/			//CoordPoint java.util.ArrayList.get(int index)第i个坐标CoordPoint
            CoordPoint startPoint = tmpPoints.get(i);
            //////
            startPoint = convertTouchCoordinates(startPoint);
            CoordPoint endPoint = tmpPoints.get(i + 1);
            endPoint = convertTouchCoordinates(endPoint);
            mCanvas.drawLine(startPoint.px, startPoint.py, endPoint.px, endPoint.py, mPaint);
        }
    }

    public int[] convertTouchCoordinates(float[] coors) {
		/*
		 * float[] : convert coordinate to fit on the screen of mobile.
		 */

        newCoords[0] = (int) (coors[0] * ((float) mCanvas.getWidth() / mIndoorMap.getRight()));
        newCoords[1] = (int) (coors[1] * ((float) mCanvas.getHeight() / mIndoorMap.getBottom()));

        Log.i("test", ((float) mCanvas.getWidth() / mIndoorMap.getRight()) + " " + ((float) mCanvas.getHeight() / mIndoorMap.getBottom()));
        return newCoords;
    }


    public CoordPoint convertTouchCoordinates(CoordPoint coors) {
		/*
		 * CoordPoint : convert coordinate to fit on the screen of mobile.
		 */
        float xtmp = coors.px * ((float) mCanvas.getWidth() / mIndoorMap.getRight());
        float ytmp = coors.py * ((float) mCanvas.getHeight() / mIndoorMap.getBottom());
        return new CoordPoint(xtmp, ytmp);
    }


    private void initBottomButton() {
        mOffLineLearnButton = (Button) findViewById(R.id.bt_offline_learn);
        mShowPositionButton = (Button) findViewById(R.id.bt_show_position);
        mSettingsButton = (Button) findViewById(R.id.bt_settings);

        resetBottom();
        mShowPositionButton.setTextColor(getResources().getColor(R.color.my_green));

        mBottomButtonClickListener = new BottomButtonClickListener();
        mSettingsButton.setOnClickListener(mBottomButtonClickListener);
        mOffLineLearnButton.setOnClickListener(mBottomButtonClickListener);
//        mSettingsButton.setOnClickListener(mBottomButtonClickListener);
    }

    private class BottomButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {


            switch (v.getId()) {

                case R.id.bt_settings:
                    startActivity(new Intent(ShowPositionActivity.this, SettingActivity.class));
                    finish();
                    break;
                case R.id.bt_offline_learn:
                    startActivity(new Intent(ShowPositionActivity.this, OffLineLearnActivity.class));
                    finish();
                    break;
            }
        }
    }


    private void resetBottom() {

        mShowPositionButton.setTextColor(getResources().getColor(android.R.color.black));
        mSettingsButton.setTextColor(getResources().getColor(android.R.color.black));
        mOffLineLearnButton.setTextColor(getResources().getColor(android.R.color.black));
    }
}
