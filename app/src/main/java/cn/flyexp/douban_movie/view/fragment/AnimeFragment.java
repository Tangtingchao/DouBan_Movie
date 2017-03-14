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
import cn.flyexp.douban_movie.presenter.AnimePresenter;
import cn.flyexp.douban_movie.view.iview.IAnimeView;


/**
 * Created by Won on 2016/5/4.
 */
public class AnimeFragment extends BaseLazyFragment <IAnimeView, AnimePresenter> implements IAnimeView, View.OnClickListener {

    private static final String TAG = "AnimeFragment";

    @BindView(R.id.rv_anime)
    RecyclerView rv;
    @BindView(R.id.srl_anime)
    SwipeRefreshLayout srl;
    @BindView(R.id.tv_tip_anime)
    TextView tvTip;

    //判断是否第一次显示
    private boolean isFirst = true;
    //电影条目列表
    private ArrayList<MovieModel.SubjectsBean> movieModelBeans = new ArrayList<>();
    //添加FooterView的适配器
    private HeaderAndFooterWrapper animeWrapperAdapter;
    //适配器
    private MovieAdapter animeAdapter;
    //footerView文字显示
    private TextView animeFooterViewInfo;
    
    @Override
    protected AnimePresenter initPresenter() {
        return new AnimePresenter();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.main_anime;
    }

    @Override
    protected void initView() {
        //配置SwipeRefreshLayout
        srl.setEnabled(false);
        srl.setColorSchemeResources(R.color.colorAnime);
        //适配器
        animeAdapter = new MovieAdapter(movieModelBeans);
        animeWrapperAdapter = new HeaderAndFooterWrapper(animeAdapter);
        //添加footerView
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.footerview, null);
        animeFooterViewInfo = (TextView) footerView.findViewById(R.id.footerview_info);
        animeWrapperAdapter.addFootView(footerView);
        //配置RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(getContext()));//设置list列风格
        rv.setAdapter(animeWrapperAdapter);
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
                animeFooterViewInfo.setText(getText(R.string.loading_tips));
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
            animeFooterViewInfo.setText(getText(R.string.no_data_tips));
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
        animeWrapperAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_tip_anime:
                presenter.loadingData();//重新加载
                break;
            default:
                break;
        }
    }
}
