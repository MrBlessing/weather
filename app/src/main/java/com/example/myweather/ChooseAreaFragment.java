package com.example.myweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweather.db.City;
import com.example.myweather.db.County;
import com.example.myweather.db.Province;
import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.LogUtil;
import com.example.myweather.util.RegionParseUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private View view;
    private Button back;
    private TextView titleText;
    private ListView listView;
    private List<String> dataList = new ArrayList<>();
    private ProgressDialog progressDialog;

    private ArrayAdapter<String> adapter;
    //所有省份列表
    private List<Province> allProvince;
    //被选中的省份
    private Province selectedProvince;
    //所有的城市列表
    private List<City> allCity;
    //被选中的city
    private City selectedCity;
    //所有的县列表
    private List<County> allCounty;
    //被选中的county
    private County selectedcounty;
    //当前列表状态
    private int currentType = 0;
    private static final int TYPE_PROVINCE = 1;
    private static final int TYPE_CITY = 2;
    private static final int TYPE_COUNTY = 3;
    private static final String TAG = "ChooseAreaFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_area,container);
        findView();
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvince();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (currentType){
                    case TYPE_PROVINCE :
                        selectedProvince = allProvince.get(i);
                        queryCity();
                        break;
                    case TYPE_CITY :
                        selectedCity = allCity.get(i);
                        queryCounty();
                        break;
                    case TYPE_COUNTY :
                        selectedcounty = allCounty.get(i);
                        if(getActivity() instanceof MainActivity){
                            Intent intent = new Intent(getActivity(),WeatherActivity.class);
                            intent.putExtra("county",selectedcounty.getCountyName());
                            startActivity(intent);
                            getActivity().finish();
                        }
                        if(getActivity() instanceof WeatherActivity){
                            WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                            weatherActivity.drawerLayout.closeDrawers();
                            weatherActivity.requestWeather(selectedcounty.getCountyName());
                            weatherActivity.refreshLayout.setRefreshing(true);
                        }
                        break;
                    default:
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentType){
                    case TYPE_CITY :
                        queryProvince();
                        break;
                    case TYPE_COUNTY :
                        queryCity();
                        break;
                    default:
                }
            }
        });
    }

    /*
    *查询所有省，优先查询本地，其次服务器
    * */
    private void queryProvince(){
        titleText.setText("省份");
        back.setVisibility(View.GONE);
        allProvince = DataSupport.findAll(Province.class);
        if(allProvince.size() > 0){
            dataList.clear();
            for(Province province : allProvince){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            //更新当前状态
            currentType = TYPE_PROVINCE;
            listView.setSelection(0);
        }else{
            String URL = "http://guolin.tech/api/china";
            queryFromService(URL,"province");
        }
    }
    /*
    *查询所有市，优先查询本地，其次服务器
    * */
    private void queryCity(){
        titleText.setText(selectedProvince.getProvinceName());
        back.setVisibility(View.VISIBLE);
        allCity = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(allCity.size() > 0){
            dataList.clear();
            for(City city : allCity){
                LogUtil.d(TAG,city.getCityName());
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            currentType = TYPE_CITY;
            listView.setSelection(0);
        }else {
            int provinceCode  = selectedProvince.getProvinceCode();
            String URL = "http://guolin.tech/api/china/" + provinceCode;
            queryFromService(URL,"city");
        }
    }
    /*
     *查询所有县，优先查询本地，其次服务器
     * */
    private void queryCounty(){
        titleText.setText(selectedCity.getCityName());
        allCounty = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(allCounty.size()>0){
            dataList.clear();
            for(County county : allCounty){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            currentType = TYPE_COUNTY;
            listView.setSelection(0);
        }else{
            String URL = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            queryFromService(URL,"county");
        }
    }



    private void queryFromService(String URL, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                closeProgressDialog();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "加载"+type+"失败", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                boolean result = false;
                if("province".equals(type))
                    result = RegionParseUtil.handleProvinceResponse(content);
                if("city".equals(type))
                    result = RegionParseUtil.handleCityResponse(content , selectedProvince.getId());
                if("county".equals(type)){
                    result = RegionParseUtil.handleCountyResponse(content,selectedCity.getId());
                }

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type))
                                queryProvince();
                            if("city".equals(type))
                                queryCity();
                            if("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private void findView() {
        back = view.findViewById(R.id.choose_area_back);
        titleText = view.findViewById(R.id.choose_area_titleText);
        listView = view.findViewById(R.id.choose_area_listView);
    }

    public void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public void closeProgressDialog(){
        if(progressDialog != null ){
            progressDialog.dismiss();
        }
    }

}
