package com.app.rbc.siteincharge.fragments;


import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.app.rbc.siteincharge.R;
import com.app.rbc.siteincharge.activities.StockActivity;
import com.app.rbc.siteincharge.adapters.Vehicle_detail_adapter;
import com.app.rbc.siteincharge.interfaces.ApiServices;
import com.app.rbc.siteincharge.models.Product;
import com.app.rbc.siteincharge.models.StockPoDetails;
import com.app.rbc.siteincharge.models.db.models.Categoryproduct;
import com.app.rbc.siteincharge.utils.AppUtil;
import com.app.rbc.siteincharge.utils.RetrofitClient;
import com.app.rbc.siteincharge.utils.TagsPreferences;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Stock_po_details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Stock_po_details extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "Stock_po_details";
    private static final String PO_NUMBER = "PO_NUMBER";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.profile_pic)
    SimpleDraweeView profilePic;
    @BindView(R.id.employee_name)
    TextView employeeName;
    @BindView(R.id.role)
    TextView role;
    @BindView(R.id.PO_date)
    TextView PODate;
    @BindView(R.id.PO_amount)
    TextView POAmount;
    @BindView(R.id.PO_pay_mode)
    TextView POPayMode;
    @BindView(R.id.PO_status)
    TextView POStatus;
    @BindView(R.id.source_type)
    ImageView sourceType;
    @BindView(R.id.vendor_name)
    TextView vendorName;
    @BindView(R.id.vendor_address)
    TextView vendorAddress;
    @BindView(R.id.vendor_phone)
    TextView vendorPhone;
    @BindView(R.id.vehicle_info)
    RecyclerView vehicleInfo;
    Unbinder unbinder;
    @BindView(R.id.product_table)
    TableLayout productTable;

    int count;

    // TODO: Rename and change types of parameters
    private String po_number;
    StockPoDetails stockPoDetails;


    public Stock_po_details() {
        // Required empty public constructor
    }


    public static Stock_po_details newInstance(String po_number) {
        Stock_po_details fragment = new Stock_po_details();
        Bundle args = new Bundle();
        args.putString(PO_NUMBER, po_number);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            po_number = getArguments().getString(PO_NUMBER);

        }
        StockActivity.show_tabs=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_po_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("PO number : "+po_number);
        AppBarLayout.LayoutParams toolbarParams = ( AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarParams.setScrollFlags(0);
        toolbar.setLayoutParams(toolbarParams);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        count=1;
        get_data();
    }

    SweetAlertDialog pDialog;

    private void get_data() {
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        final ApiServices apiServices = RetrofitClient.getApiService();
        // AppUtil.logger(TAG, "User id : " + user_id + " Pwd : " + new_password.getText().toString());
        Call<StockPoDetails> call = apiServices.po_details(po_number);
        //AppUtil.logger("Date :", String.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())));
        AppUtil.logger("Stock PO details ", "Get PO details : " + call.request().toString() + "PO_ID : " + po_number);
        call.enqueue(new Callback<StockPoDetails>() {
            @Override
            public void onResponse(Call<StockPoDetails> call, Response<StockPoDetails> response) {
                pDialog.dismiss();
                if (response.body().getMeta().getStatus() == 2) {


                    AppUtil.putString(getContext().getApplicationContext(), TagsPreferences.PO_DETAILS, new Gson().toJson(response.body()));
                    stockPoDetails = new Gson().fromJson(AppUtil.getString(getContext(), TagsPreferences.PO_DETAILS), StockPoDetails.class);
                    AppUtil.logger("PO Details : ", AppUtil.getString(getContext().getApplicationContext(), TagsPreferences.PO_DETAILS));
                    set_data();

//                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
//                    Date formated = null;
//                    try {
//                        formated = fmt.parse(year+"-"+month+"-01");
//                    } catch (ParseException e1) {
//                        e1.printStackTrace();
//                    }


                }

            }

            @Override
            public void onFailure(Call<StockPoDetails> call1, Throwable t) {

                pDialog.dismiss();


                AppUtil.showToast(getContext(), "Network Issue. Please check your connectivity and try again");
            }
        });
    }

    private void set_data() {

        set_po_details();
        set_vendor_details();
        set_vehicle_info();
        set_product_details();
    }

    private void set_po_details() {

        StockPoDetails.PoDetail poDetail = stockPoDetails.getPoDetails().get(0);
        if(poDetail.getDetails().get(0).getCreatedBy().toString().trim().equalsIgnoreCase(AppUtil.getString(getContext(),TagsPreferences.USER_ID)))
        {
            employeeName.setText(AppUtil.getString(getContext(),TagsPreferences.NAME));
            role.setText(AppUtil.getString(getContext(),TagsPreferences.ROLE));
            int color = getContext().getResources().getColor(R.color.black_overlay);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
            roundingParams.setBorder(color, 1.0f);
            roundingParams.setRoundAsCircle(true);
            profilePic.getHierarchy().setRoundingParams(roundingParams);


            profilePic.setImageURI(AppUtil.getString(getContext(),TagsPreferences.PROFILE_IMAGE));


        }
        else
        {
            String[] user = AppUtil.get_employee_from_user_id(getContext(), poDetail.getDetails().get(0).getCreatedBy().toString().trim());

            show_profile_pic(user);
            employeeName.setText(user[0]);
            role.setText(user[2]);
        }
        AppUtil.logger("User Details : ", poDetail.getDetails().get(0).getCreatedBy());

        PODate.setText(poDetail.getDetails().get(0).getCreationDt());
        // POQuantity.setText(poDetail.getDetails().get(0)..toString());
        POAmount.setText("Rs."+" "+poDetail.getDetails().get(0).getPrice().toString());
        POPayMode.setText(poDetail.getDetails().get(0).getPayMode());
        POStatus.setText(poDetail.getDetails().get(0).getStatus());



    }

    public void show_profile_pic(String user[]) {

        int color = getContext().getResources().getColor(R.color.black_overlay);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setBorder(color, 1.0f);
        roundingParams.setRoundAsCircle(true);
        profilePic.getHierarchy().setRoundingParams(roundingParams);


        profilePic.setImageURI(Uri.parse(user[1]));

    }

    private void set_vendor_details() {

        StockPoDetails.VendorDetail vendorDetail = stockPoDetails.getVendorDetails().get(0);
        vendorName.setText(vendorDetail.getVendorName());
        vendorAddress.setText(vendorDetail.getVendorAdd());
        vendorPhone.setText(vendorDetail.getVendorPhone().toString());
    }

    private void set_vehicle_info() {

        vehicleInfo.setNestedScrollingEnabled(false);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        vehicleInfo.setLayoutManager(gridLayoutManager);
        vehicleInfo.setItemAnimator(new DefaultItemAnimator());
        vehicleInfo.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        Vehicle_detail_adapter adapter = new Vehicle_detail_adapter(stockPoDetails.getVehicleDetails(), getContext());
        vehicleInfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void set_product_details() {

        for (int i = 0; i < stockPoDetails.getPoDetails().get(0).getProducts().size(); i++) {

            Product reqDetail = stockPoDetails.getPoDetails().get(0).getProducts().get(i);
            addrow(reqDetail.getProduct(),reqDetail.getQuantity().toString(),reqDetail.getRemQuantity().toString());
        }
    }

    private void addrow(String product, String quantity, String rem_quantity) {
        String unit = "";
        List<Categoryproduct> categoryproductList = Categoryproduct.find(Categoryproduct.class,
                "product = ?",product);
        if(categoryproductList.size() != 0) {
            unit = categoryproductList.get(0).getUnit();
        }
        View tr = getActivity().getLayoutInflater().inflate(R.layout.custom_product_row_layout,null);

        TextView productText = (TextView) tr.findViewById(R.id.product);
        TextView quantityText = (TextView) tr.findViewById(R.id.quantity);
        TextView remaining_quantity_text = (TextView) tr.findViewById(R.id.remaining_quantity);
        Button product_icon = (Button) tr.findViewById(R.id.product_icon);
        ImageView delete_icon = (ImageView) tr.findViewById(R.id.delete_icon);


        productText.setText(product);
        quantityText.setText(quantity+" "+unit);
        remaining_quantity_text.setText(rem_quantity+" "+unit);
        product_icon.setText(product.substring(0,1));
        productTable.addView(tr, count);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

