package com.ithub.mda.elearn;

import android.content.Context;
import android.database.Cursor;
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

public class ELearnLogin extends Fragment implements
    View.OnClickListener{
    TextInputLayout emailLayout, passwordLayout;
    EditText email,password;
    Button login;
    TextView needHelp,createAccount;
    OnFragmentInteractionListener mListener;
    Boolean emailTag =false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elearn_login, container, false);
        emailLayout = (TextInputLayout)view.findViewById(R.id.elearn_email_id);
        email = (EditText) emailLayout.findViewById(R.id.inneremaillogin);
        passwordLayout = (TextInputLayout)view.findViewById(R.id.elearn_password);
        password = (EditText) passwordLayout.findViewById(R.id.innerpasswordlogin);
        needHelp = (TextView)view.findViewById(R.id.textview_need_help);
        createAccount = (TextView)view.findViewById(R.id.textview_create_a_new_account);
        login = (Button)view.findViewById(R.id.elearn_login_button);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(email.getText()) || !Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    email.setTextColor(0xffff0000);
                    email.setError("Invalid Email Address");
                    emailTag = false;
                } else {
                    email.setTextColor(0xff000000);
                    emailTag = true;
                }
            }
        });
        login.setOnClickListener(this);
        needHelp.setOnClickListener(this);
        createAccount.setOnClickListener(this);
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
            case R.id.elearn_login_button:
            {
                if (emailTag) {
                    if (validUser()) {
                        mListener.onLogin();
                    } else {
                        Toast.makeText(getActivity(), "Enter valid Email Address and Password", Toast.LENGTH_SHORT).show();
                        email.setText("");
                        password.setText("");
                    }
                }else {
                    Toast.makeText(getActivity(), "Enter valid Email Address", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.textview_need_help:
            {
                mListener.needHelp();
                break;
            }
            case R.id.textview_create_a_new_account:
            {
                mListener.createAccount();
                break;
            }
        }
    }
    private boolean validUser() {
        boolean result = false;
        ELearnLocalDB eLearnLocalDB = new ELearnLocalDB(getActivity());
        SQLiteDatabase db = eLearnLocalDB.getReadableDatabase();
        Cursor cr = db.query("elearnuserdetails",new String[] {"email","password"},"",null,"","","name");
        if(cr.moveToNext())
        {
            if((cr.getString(0).equals(email.getText().toString()))&&(cr.getString(1).equals(password.getText().toString()))){
                result = true;
            }
        }
        return result;
    }
    public interface OnFragmentInteractionListener {
        void createAccount();
        void onLogin();
        void needHelp();
    }
}