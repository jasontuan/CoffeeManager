package com.tuan.coffeemanager.feature.coffee.fragment.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.tuan.coffeemanager.R;
import com.tuan.coffeemanager.base.BaseFragment;
import com.tuan.coffeemanager.constant.ConstApp;
import com.tuan.coffeemanager.feature.coffee.fragment.adapter.TableCoffeeAdapter;
import com.tuan.coffeemanager.feature.coffee.fragment.adapter.TableOrderCoffeeAdapter;
import com.tuan.coffeemanager.feature.coffee.fragment.presenter.TableCoffeePresenter;
import com.tuan.coffeemanager.feature.editOrder.EditOrderActivity;
import com.tuan.coffeemanager.feature.order.OrderActivity;
import com.tuan.coffeemanager.feature.pay.PayActivity;
import com.tuan.coffeemanager.listener.OnItemClickListener;
import com.tuan.coffeemanager.listener.ViewListener;
import com.tuan.coffeemanager.model.Order;
import com.tuan.coffeemanager.model.Table;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TableFragment extends BaseFragment implements ViewListener.ViewListDataListener<Table>, ViewListener.ViewlistDataDoubleListener<Table, Order>, NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rvTable)
    RecyclerView rvTable;
    @BindView(R.id.navTable)
    NavigationView navTable;
    @BindView(R.id.dlTable)
    DrawerLayout dlTable;
    Unbinder unbinder;
    @BindView(R.id.srTable)
    SwipeRefreshLayout srTable;

    private TableCoffeePresenter tableCoffeePresenter;
    private Table table;
    private String order_drink_id = null;

    public static TableFragment newInstance() {
        Bundle args = new Bundle();
        TableFragment fragment = new TableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dlTable.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navTable.setNavigationItemSelectedListener(this);
        rvTable.setLayoutManager(new GridLayoutManager(getContext(), 3));
        TextView tvNameUser = navTable.getHeaderView(0).findViewById(R.id.tvNameUser);
        tvNameUser.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        tableCoffeePresenter = new TableCoffeePresenter(this, this);
        tableCoffeePresenter.getTableOrderListData();

        srTable.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSuccess(final List<Table> tables) {
        final TextView tvNumberTable = navTable.getHeaderView(0).findViewById(R.id.tvNumberTable);
        final MenuItem navEditOrder = navTable.getMenu().findItem(R.id.nav_EditOrder);
        final MenuItem navOrder = navTable.getMenu().findItem(R.id.nav_Order);
        final MenuItem navPay = navTable.getMenu().findItem(R.id.nav_Pay);
        TableCoffeeAdapter tableCoffeeAdapter = new TableCoffeeAdapter(getContext(), tables);
        rvTable.setAdapter(tableCoffeeAdapter);
        tableCoffeeAdapter.notifyDataSetChanged();
        tableCoffeeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                table = tables.get(position);
                tvNumberTable.setText(getResources().getString(R.string.text_number_table, String.valueOf(position + 1)));
                navPay.setEnabled(false);
                navOrder.setEnabled(true);
                navEditOrder.setEnabled(false);
                dlTable.openDrawer(Gravity.START);
            }
        });
    }

    @Override
    public void onSuccess(final List<Table> tableList, final List<Order> orderList) {
        if (srTable != null) {
            srTable.setRefreshing(false);
        }
        if (navTable != null){
            final TextView tvNumberTable = navTable.getHeaderView(0).findViewById(R.id.tvNumberTable);
            final MenuItem navEditOrder = navTable.getMenu().findItem(R.id.nav_EditOrder);
            final MenuItem navOrder = navTable.getMenu().findItem(R.id.nav_Order);
            final MenuItem navPay = navTable.getMenu().findItem(R.id.nav_Pay);
            TableOrderCoffeeAdapter tableOrderCoffeeAdapter = new TableOrderCoffeeAdapter(getContext(), tableList, orderList);
            rvTable.setAdapter(tableOrderCoffeeAdapter);
            tableOrderCoffeeAdapter.notifyDataSetChanged();
            tableOrderCoffeeAdapter.setOnTableClickListener(new OnItemClickListener.OnTableClickListener() {
                @Override
                public void onItemOpenClickListener(int position) {
                    table = tableList.get(position);
                    tvNumberTable.setText(getResources().getString(R.string.text_number_table, String.valueOf(position + 1)));
                    navPay.setEnabled(false);
                    navOrder.setEnabled(true);
                    navEditOrder.setEnabled(false);
                    dlTable.openDrawer(Gravity.START);
                }

                @Override
                public void onItemCloseClickListener(int position) {
                    table = tableList.get(position);
                    tvNumberTable.setText(getResources().getString(R.string.text_number_table, String.valueOf(position + 1)));
                    navPay.setEnabled(true);
                    navOrder.setEnabled(false);
                    navEditOrder.setEnabled(true);
                    for (Order order : orderList) {
                        if (order.getTable_id().equals(table.getId())) {
                            order_drink_id = order.getOrder_detail_id();
                        }
                    }
                    dlTable.openDrawer(Gravity.START);
                }
            });
        }

    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_Order: {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra(ConstApp.TABLE_OBJ, table);
                startActivity(intent);
                dlTable.closeDrawer(Gravity.START);
                break;
            }
            case R.id.nav_EditOrder: {
                Intent intent = new Intent(getActivity(), EditOrderActivity.class);
                intent.putExtra(ConstApp.TABLE_OBJ, table);
                intent.putExtra(ConstApp.ORDER_DETAIL_ID, order_drink_id);
                startActivity(intent);
                dlTable.closeDrawer(Gravity.START);
                break;
            }
            case R.id.nav_Pay: {
                Intent intent = new Intent(getActivity(), PayActivity.class);
                intent.putExtra(ConstApp.TABLE_OBJ, table);
                intent.putExtra(ConstApp.ORDER_DETAIL_ID, order_drink_id);
                startActivity(intent);
                dlTable.closeDrawer(Gravity.START);
                break;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        tableCoffeePresenter.getTableOrderListData();
    }

    @Override
    public void onRefresh() {
        tableCoffeePresenter.getTableOrderListData();
    }
}
