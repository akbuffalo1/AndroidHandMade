package biz.enon.tra.uae.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import biz.enon.tra.uae.R;
import biz.enon.tra.uae.customviews.HexagonView;
import biz.enon.tra.uae.entities.HexagonViewTarget;
import biz.enon.tra.uae.fragment.HexagonHomeFragment;
import biz.enon.tra.uae.global.Service;
import biz.enon.tra.uae.rest.model.response.DynamicServiceInfoResponseModel;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class ServicesRecyclerViewAdapter extends Adapter<ServicesRecyclerViewAdapter.ViewHolder> {

    private static final int STATIC_SERVICE_TYPE = 0;
    private static final int DYNAMIC_SERVICE_TYPE = 1;

    private static final int FAKE_ITEMS_COUNT = 18;

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<Service> mDataSet;
    private final DynamicServiceInfoResponseModel.List mDynamicServices;

    private float mMarginOffset = 0;
    private HexagonHomeFragment.OnServiceSelectListener mServiceSelectListener;

    public ServicesRecyclerViewAdapter(final Context _context, final List<Service> _dataSet,
                                       final float _marginOffset) {
        mDataSet = _dataSet;
        mDynamicServices = new DynamicServiceInfoResponseModel.List();
        mContext = _context;
        mInflater = LayoutInflater.from(_context);
        mMarginOffset = _marginOffset;
    }

    public void addDynamicServices(final DynamicServiceInfoResponseModel.List _services) {
        int oldSize = getItemCount();
        mDynamicServices.addAll(_services);
        notifyItemRangeInserted(oldSize, _services.size());
    }

    public final void setServiceSelectListener(final HexagonHomeFragment.OnServiceSelectListener _serviceSelectListener) {
        mServiceSelectListener = _serviceSelectListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup _viewGroup, final int _viewType) {
        final View view = mInflater.inflate(R.layout.recycler_service_item, _viewGroup, false);
        if (_viewType == STATIC_SERVICE_TYPE) {
            return new StaticServiceViewHolder(view);
        } else {
            return new DynamicServiceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder _viewHolder, final int _position) {
        final int viewType = getItemViewType(_position);
        if(_position == 4){
            _viewHolder.setEmptyData(_position); // TODO: LIFE HACK - REMOVE THIS CONDITION WITH BODY, used to skip one empty service in layout manager
        } else if (viewType == STATIC_SERVICE_TYPE) {
            ((StaticServiceViewHolder) _viewHolder).setData(_position, mDataSet.get(_position));
        } else if (_position < mDataSet.size() + FAKE_ITEMS_COUNT) {
            _viewHolder.setEmptyData(_position);
        } else {
            final int dataPosition = _position - mDataSet.size() - FAKE_ITEMS_COUNT;
            ((DynamicServiceViewHolder) _viewHolder).setData(_position, mDynamicServices.get(dataPosition));
        }

        LayoutParams layoutParams = (LayoutParams) _viewHolder.getContainer().getLayoutParams();
        if (_position == 1 || _position == 3) {
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, mContext.getResources().getDisplayMetrics());
            layoutParams.setMargins(layoutParams.getMarginStart(), (int) mMarginOffset + margin, layoutParams.getMarginEnd(), layoutParams.bottomMargin);
            _viewHolder.getContainer().setLayoutParams(layoutParams);
        } else if (_position == 0 || _position == 2) {
            layoutParams.setMargins(layoutParams.getMarginStart(), (int) mMarginOffset, layoutParams.getMarginEnd(), layoutParams.bottomMargin);
            _viewHolder.getContainer().setLayoutParams(layoutParams);
        } else {
            layoutParams.setMargins(layoutParams.getMarginStart(), 0, layoutParams.getMarginEnd(), layoutParams.bottomMargin);
            _viewHolder.getContainer().setLayoutParams(layoutParams);
        }
    }

    @Override
    public final int getItemViewType(final int position) {
        return position < mDataSet.size() ? STATIC_SERVICE_TYPE : DYNAMIC_SERVICE_TYPE;
    }

    @Override
    public final int getItemCount() {
        return mDataSet.size() + FAKE_ITEMS_COUNT + mDynamicServices.size();
    }

    public final class StaticServiceViewHolder extends ViewHolder<Service> {

        public StaticServiceViewHolder(View v) {
            super(v);
        }

        public void setData(final int _position, @NonNull final Service _service) {
            hvHexagonView.setHexagonSrcDrawable(_service.getDrawableRes());
            textView.setText(_service.getTitleRes());
            rootView.setTag(_service);
        }

        @Override
        public void onClick(View v) {
            if (mServiceSelectListener != null && rootView.getTag() != null) {
                mServiceSelectListener.onServiceSelect((Service) rootView.getTag(), null);
            }
        }

    }

    public final class DynamicServiceViewHolder extends ViewHolder<DynamicServiceInfoResponseModel> {

        public DynamicServiceViewHolder(View v) {
            super(v);
        }

        public void setData(final int _position, @NonNull final DynamicServiceInfoResponseModel _service) {
            Picasso
                    .with(mContext)
                    .load(_service.getIconUrl(mContext))
                    .into(new HexagonViewTarget(hvHexagonView, HexagonView.ScaleType.INSIDE_CROP));
            textView.setText(_service.getServiceName());
            rootView.setTag(_service);
        }

        @Override
        public void onClick(View v) {
            if (mServiceSelectListener != null && rootView.getTag() != null) {
                DynamicServiceInfoResponseModel dynamicServiceInfo = (DynamicServiceInfoResponseModel) rootView.getTag();
                mServiceSelectListener.onServiceSelect(dynamicServiceInfo, dynamicServiceInfo);
            }
        }

    }

    protected abstract class ViewHolder<T> extends RecyclerView.ViewHolder implements OnClickListener {

        protected final View rootView;
        protected final TextView textView;
        protected final HexagonView hvHexagonView;
        protected final PercentRelativeLayout container;

        public abstract void setData(int position, @NonNull T _data);

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(this);

            rootView = v;
            textView = (TextView) v.findViewById(R.id.textView);
            hvHexagonView = (HexagonView) v.findViewById(R.id.hvHexagonView);
            container = (PercentRelativeLayout) v.findViewById(R.id.llContainer);
        }

        public View getContainer() {
            return container;
        }

        public void setEmptyData(final int _position) {
            hvHexagonView.setHexagonSrcDrawable(null);
            hvHexagonView.setVisibility(View.GONE);
            textView.setText(null);
            hvHexagonView.setTag(null);
        }
    }
}
