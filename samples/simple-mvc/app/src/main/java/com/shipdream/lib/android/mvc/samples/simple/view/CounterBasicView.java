package com.shipdream.lib.android.mvc.samples.simple.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shipdream.lib.android.mvc.NavigationManager;
import com.shipdream.lib.android.mvc.Reason;
import com.shipdream.lib.android.mvc.samples.simple.R;
import com.shipdream.lib.android.mvc.samples.simple.controller.CounterBasicController;

import javax.inject.Inject;

public class CounterBasicView extends AbstractFragment<CounterBasicController>
        implements CounterBasicController.View{

    @Inject
    private NavigationManager navigationManager;

    private TextView display;
    private TextView ipValue;
    private ProgressBar ipProgress;
    private ImageView ipRefresh;
    private Button increment;
    private Button decrement;
    private Button buttonShowAdvancedView;

    @Override
    protected Class<CounterBasicController> getControllerClass() {
        return CounterBasicController.class;
    }

    /**
     * @return Layout id used to inflate the view of this MvcFragment.
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_counter_basic;
    }

    /**
     * Lifecycle similar to onViewCreated by with more granular control with an extra argument to
     * indicate why this view is created: 1. first time created, or 2. rotated or 3. restored
     * @param view The root view of the fragment
     * @param savedInstanceState The savedInstanceState when the fragment is being recreated after
     *                           its enclosing activity is killed by OS, otherwise null including on
     *                           rotation
     * @param reason Indicates the {@link Reason} why the onViewReady is called.
     */
    @Override
    public void onViewReady(View view, Bundle savedInstanceState, Reason reason) {
        super.onViewReady(view, savedInstanceState, reason);

        display = (TextView) view.findViewById(R.id.fragment_a_counterDisplay);
        ipValue = (TextView) view.findViewById(R.id.fragment_a_ipValue);
        ipProgress = (ProgressBar) view.findViewById(R.id.fragment_a_ipProgress);
        ipRefresh = (ImageView) view.findViewById(R.id.fragment_a_ipRefresh);
        increment = (Button) view.findViewById(R.id.fragment_a_buttonIncrement);
        decrement = (Button) view.findViewById(R.id.fragment_a_buttonDecrement);
        buttonShowAdvancedView = (Button) view.findViewById(R.id.fragment_a_buttonShowAdvancedView);

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.increment(v);
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.decrement(v);
            }
        });

        buttonShowAdvancedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Use counterController to manage navigation to make navigation testable
                controller.goToDetailView(v);
                //Or we can use NavigationManager directly though it's harder to unit test on
                //controller level.
                //example:
                //navigationManager.navigateTo(v, "LocationB");
            }
        });

        if (reason.isFirstTime()) {
            CounterBasicSubView f = new CounterBasicSubView();

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_a_anotherFragmentContainer, f).commit();
        }

        ipRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.refreshIp();
            }
        });
    }


    @Override
    public void update() {
        display.setText(controller.getModel().getCount());
    }

    @Override
    public void showProgress() {
        ipProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        ipProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateIpValue(String ip) {
        ipValue.setVisibility(View.VISIBLE);
        ipValue.setText(ip);
    }

    @Override
    public void showErrorMessageToFetchIp() {
        Toast.makeText(getContext(), "Error to get your IP", Toast.LENGTH_SHORT).show();
    }
}
