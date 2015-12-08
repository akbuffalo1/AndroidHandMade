package com.uae.tra_smart_services.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uae.tra_smart_services.R;
import com.uae.tra_smart_services.adapter.ContactUsAdapter;
import com.uae.tra_smart_services.fragment.base.BaseFragment;
import com.uae.tra_smart_services.rest.model.response.ContactUsResponse;
import com.uae.tra_smart_services.rest.model.response.ContactUsResponse.List;
import com.uae.tra_smart_services.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by mobimaks on 02.12.2015.
 */
public class ContactUsFragment extends BaseFragment implements OnMapReadyCallback, OnItemSelectedListener, OnClickListener {

    private static final String KEY_CONTACT_US = "CONTACT_US";

    private Spinner sInfoSpinner;
    private MapView mvMap;
    private TextView tvAddress, tvPhone, tvEmail;

    private ContactUsAdapter mAdapter;

    private ArrayList<ContactUsResponse> mContactUsResponse;
    private GoogleMap mGoogleMap;

    public static ContactUsFragment newInstance(@NonNull final List _data) {
        ContactUsFragment fragment = new ContactUsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_CONTACT_US, _data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContactUsResponse = getArguments().getParcelableArrayList(KEY_CONTACT_US);
    }

    @Override
    protected void initViews() {
        super.initViews();
        sInfoSpinner = findView(R.id.sInfoSpinner_FCU);
        mvMap = findView(R.id.mvMap_FCU);
        mvMap.getMapAsync(this);
        tvAddress = findView(R.id.tvAddress_FCU);
        tvPhone = findView(R.id.tvPhone_FCU);
        tvEmail = findView(R.id.tvEmail_FCU);
        if (mContactUsResponse.size() == 1) {
            sInfoSpinner.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        sInfoSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvMap.onCreate(savedInstanceState);

        mAdapter = new ContactUsAdapter(getActivity(), mContactUsResponse);
        sInfoSpinner.setAdapter(mAdapter);
    }

    private void showContactUsData(@NonNull final ContactUsResponse _contactUsData) {
        tvAddress.setText(_contactUsData.poBox);
        tvPhone.setText(_contactUsData.phone);
        tvEmail.setText(_contactUsData.email);

        tvPhone.setOnClickListener(Patterns.PHONE.matcher(_contactUsData.phone).matches() ? this : null);
        tvEmail.setOnClickListener(Patterns.EMAIL_ADDRESS.matcher(_contactUsData.email).matches() ? this : null);

        invalidateMapMarker();
    }

    public void onMapReady(GoogleMap _googleMap) {
        mGoogleMap = _googleMap;
        invalidateMapMarker();
    }

    private void invalidateMapMarker() {
        if (mGoogleMap != null && sInfoSpinner != null) {
            final ContactUsResponse data = (ContactUsResponse) sInfoSpinner.getSelectedItem();
            LatLng latLng = data.getLatLng();
            if (latLng != null) {
                final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                mGoogleMap.animateCamera(cameraUpdate);
                setMapMarker(mGoogleMap, latLng);
            }
        }
    }

    private void setMapMarker(@NonNull GoogleMap _map, @NonNull LatLng _latLng) {
        int colorRGB = ImageUtils.getThemeColor(getActivity());
        float[] hslColor = new float[3];
        ColorUtils.colorToHSL(colorRGB, hslColor);
        _map.clear();
        _map.addMarker(new MarkerOptions()
                .position(_latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(hslColor[0])));
    }

    @Override
    public void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        showContactUsData(mContactUsResponse.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        final ContactUsResponse contactUsResponse = (ContactUsResponse) sInfoSpinner.getSelectedItem();
        switch (v.getId()) {
            case R.id.tvPhone_FCU:
                tryOpenPhoneDialer(contactUsResponse.phone);
                break;
            case R.id.tvEmail_FCU:
                tryOpenEmailClient(contactUsResponse.email);
                break;
        }
    }

    private void tryOpenPhoneDialer(@NonNull String _phone) {
        final Intent phoneIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + _phone));
        if (phoneIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(phoneIntent);
        }
    }

    private void tryOpenEmailClient(@NonNull String _email) {
        final Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + _email));
        if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mvMap.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mvMap.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mvMap.onDestroy();
        super.onDestroyView();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_contact_us;
    }

    @Override
    protected int getTitle() {
        return 0;
    }
}
