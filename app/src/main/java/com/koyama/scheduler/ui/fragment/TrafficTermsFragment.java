package com.koyama.scheduler.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.TrafficTermAdapter;
import com.koyama.scheduler.model.TrafficTerm;
import java.util.ArrayList;
import java.util.List;

public class TrafficTermsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_traffic_terms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        List<TrafficTerm> terms = getTrafficTerms();
        TrafficTermAdapter adapter = new TrafficTermAdapter(terms);
        recyclerView.setAdapter(adapter);
    }

    private List<TrafficTerm> getTrafficTerms() {
        List<TrafficTerm> terms = new ArrayList<>();
        terms.add(new TrafficTerm("通行止め (Tsukodome)", "Road closed"));
        terms.add(new TrafficTerm("危険物 (kikenbutsu)", "Dangerous materials"));
        terms.add(new TrafficTerm("専用 (senyou)", "Exclusive"));
        terms.add(new TrafficTerm("優先 (yusen)", "Priority"));
        terms.add(new TrafficTerm("徐行 (jokou)", "Slowing down"));
        terms.add(new TrafficTerm("止まれ (tomare)", "Stop"));
        terms.add(new TrafficTerm("通学路 (tsuugakuro)", "School zone"));
        terms.add(new TrafficTerm("前方優先道路 (zenpou-yusendoro)", "Priority road ahead or right of way"));
        terms.add(new TrafficTerm("追越し禁止 (oikoshi-kinshi)", "Don't over take"));
        terms.add(new TrafficTerm("信号機 (shingouki)", "Traffic light"));
        terms.add(new TrafficTerm("歩行者 (kousha)", "Pedestrian"));
        terms.add(new TrafficTerm("踏切 (fumikiri)", "Railway crossing"));
        terms.add(new TrafficTerm("スクールバス (sukuuru-basu)", "School bus"));
        terms.add(new TrafficTerm("仮免許証 (karimenkyoshou)", "Learner's drivers permit"));
        terms.add(new TrafficTerm("免許証 (menkyosyou)", "Driver's license"));
        terms.add(new TrafficTerm("普通乗用車 (futsuu jyouyousya)", "Regular passenger vehicle"));
        terms.add(new TrafficTerm("タクシー (takushi)", "Taxi"));
        terms.add(new TrafficTerm("貨物 (kamotsu)", "Truck"));
        terms.add(new TrafficTerm("大貨 (daika)", "Large truck"));
        terms.add(new TrafficTerm("大貨等 (daikatou)", "Large truck, specific middle-size truck, and special heavy equipment"));
        terms.add(new TrafficTerm("中貨 (chuka)", "Middle-size truck"));
        terms.add(new TrafficTerm("特定中貨 (tokutei chuka)", "Specific middle-size truck"));
        terms.add(new TrafficTerm("準中貨 (jun chuka)", "Semi-middle-size truck"));
        terms.add(new TrafficTerm("普貨 (fuka)", "Regular truck"));
        terms.add(new TrafficTerm("軽車両 (keisharyou)", "Light vehicle"));
        terms.add(new TrafficTerm("大型 (ogata)", "Large vehicle"));
        terms.add(new TrafficTerm("大型等 (oogatato)", "Large motor vehicle, specific middle motor vehicle and special heavy equipment"));
        
        // New vehicle-related terms
        terms.add(new TrafficTerm("中型 (chuugata)", "Middle motor vehicle"));
        terms.add(new TrafficTerm("特定中型 (tokuteichuugata)", "Specific middle motor vehicle"));
        terms.add(new TrafficTerm("準中型 (junjugata)", "Semi-middle motor vehicle"));
        terms.add(new TrafficTerm("普通 (futsuu)", "Regular motor vehicle"));
        terms.add(new TrafficTerm("大特 (daitoku)", "Special heavy equipment"));
        terms.add(new TrafficTerm("自動二輪 (jidounirin)", "Large and regular motorcycle"));
        terms.add(new TrafficTerm("軽 (kei)", "total displacement of less than 660cc"));
        terms.add(new TrafficTerm("小特 (kotoku)", "Special light equipment"));
        terms.add(new TrafficTerm("原付 (gentsuki)", "General motorized bicycle"));
        terms.add(new TrafficTerm("特定原付 (tokuteigentsuki)", "Specified small motorized bicycle"));
        terms.add(new TrafficTerm("特例特定原付 (tokureitokuteigentsuki)", "Exceptional specified small motorized bicycle"));
        terms.add(new TrafficTerm("二輪 (nirin)", "2-wheel motor vehicle and general motorized bicycle"));
        terms.add(new TrafficTerm("小二輪 (syonirin)", "Light motorcycle and general motorized bicycle"));
        terms.add(new TrafficTerm("自転車 (jitensya)", "Regular bicycle"));
        terms.add(new TrafficTerm("乗用 (jouyou)", "Motor vehicle for transporting passengers"));
        terms.add(new TrafficTerm("大乗 (daijou)", "Large-size passenger vehicle"));
        terms.add(new TrafficTerm("中乗 (chujou)", "Middle-size passenger vehicle"));
        terms.add(new TrafficTerm("特定中乗 (tokuteichujou)", "Specific middle-size passenger vehicle"));
        terms.add(new TrafficTerm("準中乗 (junchujou)", "Semi-middle-size passenger vehicle"));
        terms.add(new TrafficTerm("普乗 (fujou)", "Regular passenger vehicle"));
        terms.add(new TrafficTerm("マイクロ (maikuro)", "Large size passenger vehicle and specific middle-size passenger vehicle other than large bus"));
        terms.add(new TrafficTerm("路線バス (rosen basu)", "general transportation of passengers"));
        terms.add(new TrafficTerm("バス (basu)", "Bus, passenger vehicle"));
        terms.add(new TrafficTerm("大型バス (oogata basu)", "Large passenger vehicle"));
        terms.add(new TrafficTerm("けん引 (kenin)", "Towing, motor vehicle towing a vehicle total weight exceeding of 750kg"));
        
        return terms;
    }
} 