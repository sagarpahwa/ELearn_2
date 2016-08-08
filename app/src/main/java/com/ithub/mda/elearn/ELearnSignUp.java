package com.ithub.mda.elearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class ELearnSignUp extends Fragment implements View.OnClickListener{
    TextInputLayout lname,lemail,lpass,lconfirmpass;
    EditText name,email,pass,confirmpass;
    Button signup;
    TextView haveAccount;
    Boolean emailTag=false;
    OnFragmentInteractionListener mListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elearn_sign_up, container, false);
        lname = (TextInputLayout) view.findViewById(R.id.username_elearn);
        lemail = (TextInputLayout) view.findViewById(R.id.email_id_elearn);
        lpass = (TextInputLayout) view.findViewById(R.id.password_elearn);
        lconfirmpass = (TextInputLayout) view.findViewById(R.id.confirm_password_elearn);
        name = (EditText) lname.findViewById(R.id.innernamesignup);
        email = (EditText) lemail.findViewById(R.id.inneremailsignup);
        pass = (EditText) lpass.findViewById(R.id.innerpasssignup);
        confirmpass = (EditText) lconfirmpass.findViewById(R.id.innerconfirmsignup);
        signup = (Button) view.findViewById(R.id.elearn_signup_button);
        haveAccount = (TextView)view.findViewById(R.id.textview_already_have_an_account);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(email.getText()) || !/*isValidEmailAddress(email.getText().toString())*/Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    email.setTextColor(0xffff0000);
                    email.setError("Invalid Email Address");
                    emailTag = false;
                    //email.setTag(0, new String("false"));
                }
                else {
                    emailTag = true;
                    email.setTextColor(0xff000000);
                }
                  //  email.setTag(0, new String("true"));
            }
        });
        signup.setOnClickListener(this);
        haveAccount.setOnClickListener(this);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.elearn_signup_button:
            {
                if (pass.getText().toString().equals(confirmpass.getText().toString())) {
                    if (emailTag) {
                        ELearnLocalDB eLearnLocalDB = new ELearnLocalDB(getActivity());
                        SQLiteDatabase db = eLearnLocalDB.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("name", name.getText().toString());
                        cv.put("profilepic", "");
                        cv.put("email", email.getText().toString());
                        cv.put("password", pass.getText().toString());
                        long l = db.insert("elearnuserdetails", "", cv);
                        if (l > 0) {
                            Toast.makeText(getActivity(), "Record Inserted Successfully!", Toast.LENGTH_SHORT).show();
                            mListener.onSignUp();
                        }
                    }else {
                        Toast.makeText(getActivity(),"Enter valid Email Address",Toast.LENGTH_SHORT).show();
                        email.setText("");
                    }
                } else{
                    Toast.makeText(getActivity(),"Both Password must match",Toast.LENGTH_SHORT).show();
                    pass.setText("");
                    confirmpass.setText("");
                }
                break;
            }
            case R.id.textview_already_have_an_account:
            {
                mListener.haveAccount();
                break;
            }
        }
    }
    public interface OnFragmentInteractionListener {
        void onSignUp();
        void haveAccount();
    }
}
