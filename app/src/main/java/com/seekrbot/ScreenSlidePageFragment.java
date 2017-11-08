package com.seekrbot;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by silver on 11/11/16.
 */
public class ScreenSlidePageFragment extends Fragment {


    public static final String ARG_PAGE = "page";
    public  int mPageNumber;

    public ViewGroup m_rootView;

    private TextView dynamic_title;

    public  static  ScreenSlidePageFragment create(int mPageNumber)
    {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, mPageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Existence-Light.ttf");

        ViewGroup rootView = null;
        if (mPageNumber == 0) {
            rootView = (ViewGroup) inflater.inflate(R.layout.page_1, container, false);
            TextView title = (TextView)rootView.findViewById(R.id.tv_title);
            title.setTypeface(font);


            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < Global.sacredlist.length; i++)
            {
                arrayList.add(Global.sacredlist[i]);
            }

            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            final SeekrCellAdapter adapter = new SeekrCellAdapter(arrayList, this.getContext());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (int i = 0; i < adapter._sacredCellArray.size(); i++) {
                        SeekrCellAdapter.ViewHolder cell = adapter._sacredCellArray.get(i);
                        cell.checkImage.setImageResource(R.drawable.unchecked);

                        if (i == position) {
                            cell.checkImage.setImageResource(R.drawable.checked);
                            Global.sacredName = cell.txtSelection.getText().toString();
                            Global.sacredName =Global.sacredName.replace(Global.sacredName.substring(1), Global.sacredName.substring(1).toLowerCase());

                        }
                    }

                        }
                    });


        }
        else if(mPageNumber == 1) {
            rootView = (ViewGroup) inflater.inflate(R.layout.page_2, container, false);
            dynamic_title = (TextView)rootView.findViewById(R.id.tv_title);
            dynamic_title.setTypeface(font);

            dynamic_title.setText("what are you seeking " + Global.sacredName);

            TextView sub_title = (TextView) rootView.findViewById(R.id.sub_title);
            sub_title.setTypeface(font);

            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < Global.attributeNames.length; i++)
            {
                arrayList.add(Global.attributeNames[i]);
            }

            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            final SeekrCellAdapter adapter = new SeekrCellAdapter(arrayList, this.getContext());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (int i = 0; i < adapter._sacredCellArray.size(); i++) {
                        SeekrCellAdapter.ViewHolder cell = adapter._sacredCellArray.get(i);
                        cell.checkImage.setImageResource(R.drawable.unchecked);
                        if (i == position) {
                            cell.checkImage.setImageResource(R.drawable.checked);
                            Global.attributeName = cell.txtSelection.getText().toString();

                        }
                    }


                }
            });

        }else if(mPageNumber == 2) {
            rootView = (ViewGroup) inflater.inflate(R.layout.page_3, container, false);
            TextView title = (TextView)rootView.findViewById(R.id.tv_title);
            title.setTypeface(font);

            TextView description = (TextView)rootView.findViewById(R.id.tv_description);
            description.setTypeface(font);
        }

        return rootView;
    }

    public void setSacredName()
    {
        if (dynamic_title != null)
            dynamic_title.setText("what are you seeking " + Global.sacredName);

    }
}
