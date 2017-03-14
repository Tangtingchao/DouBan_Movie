package cn.flyexp.douban_movie.view.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import cn.flyexp.douban_movie.R;
import cn.flyexp.douban_movie.adapter.MovieAdapter;
import cn.flyexp.douban_movie.base.BaseLazyFragment;
import cn.flyexp.douban_movie.model.MovieModel;
import cn.flyexp.douban_movie.presenter.Top250Presenter;
import cn.flyexp.douban_movie.view.iview.ITop250View;


/**
 * Created by Won on 2016/5/4.
 */
public class Top250Fragment extends BaseLazyFragment<ITop250View , Top250Presenter> implements ITop250View, View.OnClickListener {

    private static final String TAG = "Top250Fragment";

    @BindView(R.id.rv_top250)
    RecyclerView rv;
    @BindView(R.id.srl_top250)
    SwipeRefreshLayout srl;
    @BindView(R.id.tv_tip_top250)
    TextView tvTip;

    //判断是否第一次显示
    private boolean isFirst = true;
    //电影条目列表
    private ArrayList<MovieModel.SubjectsBean> movieModelBeans = new ArrayList<>();
    //添加FooterView的适配器
    private HeaderAndFooterWrapper top250WrapperAdapter;
    //适配器
    private MovieAdapter top250Adapter;
    //footerView文字显示
    private TextView top250FooterViewInfo;
    
    @Override
    protected Top250Presenter initPresenter() {
        return new Top250Presenter();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.main_top250;
    }

    @Override
    protected void initView() {
        //配置SwipeRefreshLayout
        srl.setEnabled(false);
        srl.setColorSchemeResources(R.color.colorTop250);
        //适配器
        top250Adapter = new MovieAdapter(movieModelBeans);
        top250WrapperAdapter = new HeaderAndFooterWrapper(top250Adapter);
        //添加footerView
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.footerview, null);
        top250FooterViewInfo = (TextView) footerView.findViewById(R.id.footerview_info);
        top250WrapperAdapter.addFootView(footerView);
        //配置RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(getContext()));//设置list列风格
        rv.setAdapter(top250WrapperAdapter);
        //监听列表上拉
        rv.addOnScrollListener(mOnScrollListener);
        //监听点击提示文本
        tvTip.setOnClickListener(this);
    }

    /**
     * 实现懒加载,当屏幕显示这个界面的时候才会触发次方法
     */
    @Override
    protected void lazyLoad() {
        //显示数据
        if (isFirst & isPrepared && isVisible) {
            isFirst = false;
            //加载数据
            presenter.loadingData();
        }
    }

    /**
     * 上拉加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1) && dy != 0 && !srl.isRefreshing()) {// 手指不能向上滑动了并且不在加载状态
                top250FooterViewInfo.setText(getText(R.string.loading_tips));
                srl.setRefreshing(true);
                presenter.loadingData();//刷新
            }
        }
    };

    @Override
    public void showLoading() {
        srl.setRefreshing(true);
    }

    @Override
    public void showEmpty() {
        srl.setRefreshing(false);
        if (movieModelBeans.size() > 0) {
            top250FooterViewInfo.setText(getText(R.string.no_data_tips));
        } else {
            tvTip.setText(getText(R.string.empty_tips));
        }
    }

    @Override
    public void showError() {
        srl.setRefreshing(false);
        if (movieModelBeans.size() > 0) {
            Toast.makeText(getContext(), getText(R.string.error_tips), Toast.LENGTH_SHORT).show();
        } else {
            tvTip.setText(getText(R.string.error_tips2));
        }
    }

    @Override
    public void showComplete(ArrayList<?> modelBeans) {
        tvTip.setVisibility(View.GONE);
        srl.setRefreshing(false);
        movieModelBeans.addAll((Collection<? extends MovieModel.SubjectsBean>) modelBeans);
        top250WrapperAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_tip_top250:
                presenter.loadingData();//重新加载
                break;
            default:
                break;
        }
    }
}
