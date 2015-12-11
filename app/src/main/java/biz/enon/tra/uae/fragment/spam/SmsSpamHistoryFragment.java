package biz.enon.tra.uae.fragment.spam;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.enon.tra.uae.R;

import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.adapter.SpamHistoryAdapter;
import biz.enon.tra.uae.adapter.SpamHistoryAdapter.OnDeleteClickListener;
import biz.enon.tra.uae.fragment.base.BaseFragment;
import biz.enon.tra.uae.util.SmsUtils;

/**
 * Created by mobimaks on 30.09.2015.
 */
public class SmsSpamHistoryFragment extends BaseFragment implements OnDeleteClickListener {

    private RecyclerView rvSpamHistory;

    private SpamHistoryAdapter mHistoryAdapter;
    private LinearLayoutManager mHistoryLayoutManager;

    public static SmsSpamHistoryFragment newInstance() {
        return new SmsSpamHistoryFragment();
    }

    @Override
    protected void initViews() {
        super.initViews();
        rvSpamHistory = findView(R.id.rvSpamHistory_FSSH);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSpamHistoryList();
    }

    private void initSpamHistoryList() {
        List<String> stubData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            stubData.add("Promotion #" + (i + 1));
        }

        mHistoryAdapter = new SpamHistoryAdapter(getActivity(), stubData);
        mHistoryAdapter.setDeleteClickListener(this);
        rvSpamHistory.setAdapter(mHistoryAdapter);

        mHistoryLayoutManager = new LinearLayoutManager(getActivity());
        rvSpamHistory.setLayoutManager(mHistoryLayoutManager);
    }

    @Override
    public final void onDeleteClick(final String _data, final int _position) {
        mHistoryAdapter.deleteItem(_position);
        SmsUtils.sendUnblockSms(getActivity(), "no name");
    }

    @Override
    protected int getTitle() {
        return 0;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_sms_spam_history;
    }
}
