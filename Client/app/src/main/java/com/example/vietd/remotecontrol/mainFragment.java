package com.example.vietd.remotecontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link mainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link mainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainFragment extends Fragment{

    //For Closing Socket if Fragment ist Destroyed
    boolean destroyed = false;

    //Fullscreen Mode for Button
    boolean fullscreen = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public mainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static mainFragment newInstance(String param1, String param2) {
        mainFragment fragment = new mainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Connect to Server
        try {
            destroyed = false;
            connectToServer();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Error", "Server Error!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        final FloatingActionButton fullscreen_btn = view.findViewById(R.id.fullscreen_btn);
        fullscreen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView title = view.findViewById(R.id.title_text);
                TabLayout tabs = getActivity().findViewById(R.id.tabs);
                ViewPager viewPager = getActivity().findViewById(R.id.pager);
                if(!fullscreen){
                    tabs.setVisibility(View.GONE);
                    title.setVisibility(View.GONE);
                    fullscreen = true;
                }else{
                    tabs.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    fullscreen = false;
                }

            }
        });
        /*view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                return true;
            }
        });*/
        // Inflate the layout for this fragment
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    //Handle Touch Events
    /*int x = 0;
    int prev_x = 0;
    long startTime = 0;
    long endTime = 0;
    int action = MotionEvent.ACTION_DOWN;
    public boolean onTouchEvent(MotionEvent event) {
        x = 0;

        switch (action & event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                prev_x= (int)event.getX();

            case MotionEvent.ACTION_MOVE:
                action = event.getAction();
                //Set Refresh-Time
                if((endTime-startTime)>600)
                    action = MotionEvent.ACTION_DOWN;
                x = (int)event.getX();
            case MotionEvent.ACTION_UP:

        }

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                    data_out.write(prev_x-x);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            }); thread.start();

        endTime = System.currentTimeMillis();
        return false;
    }*/ 

    //Connect to Server
    Socket socket = null;
    DataInputStream data_in = null;
    DataOutputStream data_out = null;
    protected void connectToServer() throws IOException {
        final String ip_address = getActivity().getIntent().getExtras().getString("ip_address");
        final int port = getActivity().getIntent().getExtras().getInt("port");


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Create Streams

                try {
                    socket = new Socket(ip_address, port);
                    data_in = new DataInputStream(socket.getInputStream());
                    data_out = new DataOutputStream(socket.getOutputStream());


                    //Declare all important variables
                    int size, readBytes;
                    Base64.Decoder decoder = Base64.getDecoder();

                    //Get width and height of display
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                    final int height = displayMetrics.heightPixels;
                    final int width = displayMetrics.widthPixels;

                    while (!destroyed) {
                        //Connection shouldn't close because end of the stream
                        data_out.writeByte(1);
                        try {
                            //Create image
                            size = data_in.readInt();
                            byte[] img_bytes = new byte[size];
                            readBytes = 0;
                            while (readBytes != size)
                                readBytes += data_in.read(img_bytes, readBytes, size - readBytes);

                            //Decode sent image bytes
                            img_bytes = decoder.decode(img_bytes);

                            //Show Image
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(img_bytes, 0, img_bytes.length); //Array length of img_bytes increased cause of Decoding
                            if (!destroyed)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!destroyed){
                                            ImageView imageView = getActivity().findViewById(R.id.screenModeView);
                                            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
                                        }
                                    }
                                });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        data_in.close();
                        Log.d("catlog", "Socket Closed!!!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
