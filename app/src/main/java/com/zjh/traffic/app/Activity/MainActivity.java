package com.zjh.traffic.app.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjh.traffic.R;
import com.zjh.traffic.app.Application.App;
import com.zjh.traffic.app.Fragment.AccountFragment;
import com.zjh.traffic.app.Fragment.BuscxFragment;
import com.zjh.traffic.app.Fragment.PersonalcenterFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private LinearLayout menuLayout;
    private Toolbar toolbar;

    private ListView menuList;
    private List<String> list;


    private TextView title, batchRecharge, rechargeRecord;

    /**
     * 管理器类 FragmentManager 和 事务类 FragmentTransaction
     * 管理fragment
     */
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private AccountFragment accountFragment;
    private BuscxFragment buscxFragment;
    private PersonalcenterFragment personalcenterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        title.setText(list.get(0));
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, accountFragment).commit();
        batchRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击", Toast.LENGTH_SHORT).show();
            }
        });
        rechargeRecord.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitTransaction")
            @Override
            public void onClick(View v) {
                title.setText(list.get(2));
                batchRecharge.setVisibility(View.GONE);
                rechargeRecord.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.frameLayout, new PersonalcenterFragment(1)).commit();
            }
        });

    }

    /***
     * 初始化UI
     */
    private void initView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        menuLayout = findViewById(R.id.menuLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        title = findViewById(R.id.title);
        batchRecharge = findViewById(R.id.batchRecharge);
        rechargeRecord = findViewById(R.id.rechargeRecord);
        menuList = findViewById(R.id.menuList);
        list = new ArrayList();
        list.add("账户管理");
        list.add("公交查询");
        list.add("个人中心");
        list.add("退出登录");
        menuList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, list));
        menuList.setOnItemClickListener(this);

        accountFragment = new AccountFragment();
        buscxFragment = new BuscxFragment();
        personalcenterFragment = new PersonalcenterFragment();
    }

    /**
     * 侧滑菜单点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                title.setText(list.get(0));
                batchRecharge.setVisibility(View.VISIBLE);
                rechargeRecord.setVisibility(View.VISIBLE);
                fragmentTransaction.replace(R.id.frameLayout, accountFragment);
                break;
            case 1:
                title.setText(list.get(1));
                batchRecharge.setVisibility(View.GONE);
                rechargeRecord.setVisibility(View.GONE);
                fragmentTransaction.replace(R.id.frameLayout, buscxFragment);
                break;
            case 2:
                title.setText(list.get(2));
                batchRecharge.setVisibility(View.GONE);
                rechargeRecord.setVisibility(View.GONE);
                fragmentTransaction.replace(R.id.frameLayout, personalcenterFragment);
                break;
            case 3:
                App.showAlertDialog(MainActivity.this, "提醒", "确定退出登录吗",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                App.setAutoLogin(false);
                                MainActivity.this.finish();
                            }
                        });
                break;
        }
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(menuLayout);
    }
}
