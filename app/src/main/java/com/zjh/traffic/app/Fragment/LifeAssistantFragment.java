package com.zjh.traffic.app.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjh.traffic.R;
import com.zjh.traffic.app.Application.App;
import com.zjh.traffic.app.Callback.OnResponseListener;
import com.zjh.traffic.app.Chart.AverageTemperatureChart;
import com.zjh.traffic.app.Request.GetAllSenseRequest;
import com.zjh.traffic.app.Request.GetSenseByNameRequest;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 生活助手
 */
public class LifeAssistantFragment extends Fragment {
    private TextView temperature_now, temperature_today, UVI_intensity, UVI_data,
            cold_intensity, cold_data, dress_intensity, dress_data, sports_intensity,
            sports_data, air_intensity, air_data;
    private ImageButton btn_refresh;
    private TimerTask task = null;
    private Timer timer = null;

    private LinearLayout weatherchart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lifeassistant, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        upDataWeather();
        initTask();
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.cancel();
            timer = new Timer();
        }
        timer.schedule(task, 0, 3 * 1000);
    }


    private void initView(View view) {
        temperature_now = view.findViewById(R.id.temperature_now);
        temperature_today = view.findViewById(R.id.temperature_today);
        UVI_intensity = view.findViewById(R.id.UVI_intensity);
        UVI_data = view.findViewById(R.id.UVI_data);
        cold_intensity = view.findViewById(R.id.cold_intensity);
        cold_data = view.findViewById(R.id.cold_data);
        dress_intensity = view.findViewById(R.id.dress_intensity);
        dress_data = view.findViewById(R.id.dress_data);
        sports_intensity = view.findViewById(R.id.sports_intensity);
        sports_data = view.findViewById(R.id.sports_data);
        air_intensity = view.findViewById(R.id.air_intensity);
        air_data = view.findViewById(R.id.air_data);
        btn_refresh = view.findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDataWeather();
            }
        });

        weatherchart = view.findViewById(R.id.weatherchart);
        AverageTemperatureChart averageTemperatureChart = new AverageTemperatureChart();
        weatherchart.addView(averageTemperatureChart.getChartView(getContext()));
    }

    private void initTask() {
        task = new TimerTask() {
            @Override
            public void run() {
                new GetAllSenseRequest().setParams(new Object[]{App.getUserName()})
                        .sendRequest(new OnResponseListener() {
                            @Override
                            public void onResponse(Object result) {
                                try {
                                    List<Integer> list = (List<Integer>) result;
                                    upDataLifeIndex(list);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void upDataLifeIndex(List<Integer> list) {
        String[] UVI_array = {"辐射较弱，涂擦SPF12~15、PA+护肤品", "涂擦 SPF 大于 15、PA+防晒护肤品", "尽量减少外出，需要涂抹高倍数防晒霜"};
        String[] cold_array = {"温度低，风较大，较易发生感冒，注意防护", "无明显降温，感冒机率较低"};
        String[] dress_array = {"建议穿长袖衬衫、单裤等服装", "建议穿短袖衬衫、单裤等服装", "适合穿 T 恤、短薄外套等夏季服装"};
        String[] sports_array = {"气候适宜，推荐您进行户外运动", "易感人群应适当减少室外活动", "空气氧气含量低，请在室内进行休闲运动"};
        String[] air_array = {"空气质量非常好，非常适合户外活动，趁机出去多呼吸新鲜空气", "易感人群应适当减少室外活动", "空气质量差，不适合户外活动"};

        //温度
        if (list.get(0) < 8) {
            cold_intensity.setText("较易发(" + list.get(0) + ")");
            cold_data.setText(cold_array[0]);
        } else {
            cold_intensity.setText("少发(" + list.get(0) + ")");
            cold_data.setText(cold_array[1]);
        }
        if (list.get(0) < 12) {
            dress_intensity.setText("冷(" + list.get(0) + ")");
            dress_data.setText(dress_array[0]);
        } else if (list.get(0) >= 12 && list.get(0) <= 21) {
            dress_intensity.setText("舒适(" + list.get(0) + ")");
            dress_data.setText(dress_array[1]);
        } else {
            dress_intensity.setText("热(" + list.get(0) + ")");
            dress_data.setText(dress_array[2]);
        }
        //pm2.5
        if (list.get(2) > 0 && list.get(2) < 30) {
            air_intensity.setText("优(" + list.get(2) + ")");
            air_data.setText(air_array[0]);
        } else if (list.get(2) >= 30 && list.get(2) <= 100) {
            air_intensity.setText("良(" + list.get(2) + ")");
            air_data.setText(air_array[1]);
        } else {
            air_intensity.setText("污染(" + list.get(2) + ")");
            air_data.setText(air_array[2]);
        }
        //光照强度
        if (list.get(3) > 0 && list.get(3) < 1000) {
            UVI_intensity.setText("弱(" + list.get(3) + ")");
            UVI_data.setText(UVI_array[0]);
        } else if (list.get(3) >= 1000 && list.get(3) <= 3000) {
            UVI_intensity.setText("中等(" + list.get(3) + ")");
            UVI_data.setText(UVI_array[1]);
        } else {
            UVI_intensity.setText("强(" + list.get(3) + ")");
            UVI_data.setText(UVI_array[2]);
        }
        //二氧化碳
        if (list.get(4) > 0 && list.get(4) < 3000) {
            sports_intensity.setText("弱(" + list.get(4) + ")");
            sports_data.setText(sports_array[0]);
        } else if (list.get(4) >= 3000 && list.get(4) <= 6000) {
            sports_intensity.setText("中等(" + list.get(4) + ")");
            sports_data.setText(sports_array[1]);
        } else {
            sports_intensity.setText("强(" + list.get(4) + ")");
            sports_data.setText(sports_array[2]);
        }
    }

    private void upDataWeather() {
        new GetSenseByNameRequest().setParams(new Object[]{"temperature", App.getUserName()})
                .sendRequest(new OnResponseListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Object result) {
                        try {
                            temperature_now.setText(result.toString() + "°");
                            temperature_today.setText("今天：" + (Integer.parseInt(result.toString()) - 10) + "-"
                                    + (Integer.parseInt(result.toString()) + 10) + "℃");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
