package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.enon.tra.uae.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import biz.enon.tra.uae.entities.dynamic_service.BaseInputItem;
import biz.enon.tra.uae.entities.dynamic_service.InputItemsPage;
import biz.enon.tra.uae.entities.dynamic_service.input_item.AttachmentInputItem;
import biz.enon.tra.uae.interfaces.OnOpenAttachmentPickerListener;

/**
 * Created by mobimaks on 10.11.2015.
 */
public final class DynamicServicePageAdapter extends PagerAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;

    private final String mBtnText;
    private final List<InputItemsPage> mInputItemsPages;

    private Button btnSend;
    private SoftReference<? extends ViewGroup> mLastPageContainer;

    private OnClickListener mBtnSendClickListener;
    private OnOpenAttachmentPickerListener mAttachmentPickerListener;

    public DynamicServicePageAdapter(final Context _context, final String _btnSendText) {
        this(_context, null, _btnSendText);
    }

    public DynamicServicePageAdapter(final Context _context, final List<InputItemsPage> _inputItemsPages, final String _btnSendText) {
        mContext = _context;
        mInflater = LayoutInflater.from(_context);
        mInputItemsPages = (_inputItemsPages == null) ? new ArrayList<InputItemsPage>() : _inputItemsPages;
        mBtnText = _btnSendText;
    }

    public void setAttachmentCallback(OnOpenAttachmentPickerListener _listener) {
        mAttachmentPickerListener = _listener;
        for (InputItemsPage inputItemsPage : mInputItemsPages) {
            setAttachmentCallbackIntoPage(inputItemsPage);
        }
    }

    public void setSendButtonClickListener(@Nullable OnClickListener _btnSendClickListener) {
        mBtnSendClickListener = _btnSendClickListener;
        if (btnSend != null) {
            btnSend.setOnClickListener(_btnSendClickListener);
        }
    }

    public void addPage(InputItemsPage _page) {
        removeOldSendBtnIfNeed();
        mInputItemsPages.add(_page);
        notifyDataSetChanged();
    }

    private void removeOldSendBtnIfNeed() {
        final ViewGroup previousLastPageContainer;
        if (mLastPageContainer != null && (previousLastPageContainer = mLastPageContainer.get()) != null) {
            previousLastPageContainer.removeView(btnSend);
            mLastPageContainer.clear();
        }
    }

    private void setAttachmentCallbackIntoPage(InputItemsPage _page) {
        for (BaseInputItem inputItem : _page.inputItems) {
            if (inputItem.isAttachmentItem()) {
                ((AttachmentInputItem) inputItem).setAttachmentCallback(mAttachmentPickerListener);
            }
        }
    }

    @Override
    public int getCount() {
        return mInputItemsPages.size();
    }

    @Override
    public View instantiateItem(ViewGroup pageContainer, int position) {
//        final View pageView = mInflater.inflate(R.layout.layout_dynamic_service_page, pageContainer, false);
//        pageContainer.addView(pageView);
//
//        ViewHolder holder = (ViewHolder) pageView.getTag();
//        if (holder == null) {
//            holder = new ViewHolder(pageView);
//        }
//
//        final LinearLayout inputItemsContainer = holder.inputItemsContainer;
//        final InputItemsPage currentPage = mInputItemsPages.get(position);
//
//        final List<BaseInputItem> inputItems = currentPage.inputItems;
//        for (BaseInputItem inputItem : inputItems) {
//            View view = inputItem.getView(mInflater, inputItemsContainer);
//            inputItemsContainer.addView(view);
//        }
//
//        if (isLastItemPosition(position)) {
//            mLastPageContainer = new SoftReference<>(inputItemsContainer);
//            inputItemsContainer.addView(getSendBtn(inputItemsContainer));
//        }
//
//        setAttachmentCallbackIntoPage(currentPage);

//        return pageView;
        return null;
    }

    private Button getSendBtn(LinearLayout _inputItemsContainer) {
        if (btnSend == null) {
            btnSend = (Button) mInflater.inflate(R.layout.input_item_button, _inputItemsContainer, false);
            btnSend.setText(mBtnText);
            btnSend.setOnClickListener(mBtnSendClickListener);
        }
        return btnSend;
    }

    private boolean isLastItemPosition(int position) {
        return position == (getCount() - 1);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        final InputItemsPage page = mInputItemsPages.get(position);
//        final ViewGroup pageView = (ViewGroup) object;
//        final LinearLayout inputItemsContainer = ((ViewHolder) pageView.getTag()).inputItemsContainer;
//        for (BaseInputItem inputItem : page.inputItems) {
//            inputItemsContainer.removeView(inputItem.getView(mInflater, inputItemsContainer));
//        }
//
//        if (isLastItemPosition(position)) {
//            inputItemsContainer.removeView(getSendBtn(inputItemsContainer));
//        }
//        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private final class ViewHolder {

//        final LinearLayout inputItemsContainer;

        public ViewHolder(@NonNull final View _view) {
//            inputItemsContainer = (LinearLayout) _view.findViewById(R.id.pllContainer_LDSP);
//            _view.setTag(this);
        }

    }
}
