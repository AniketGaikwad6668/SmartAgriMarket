package com.smartagrimarket.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PriceController {

    @GetMapping("/prices")
    public List<Map<String, Object>> getPrices(@RequestParam String crop) {
        return generateData(crop);
    }

    @GetMapping("/recommend")
    public Map<String, Object> recommend(@RequestParam String crop) {
        return generateData(crop).stream()
                .max(Comparator.comparing(p -> (Double) p.get("modal")))
                .orElse(null);
    }

    @GetMapping("/predict")
    public Map<String, Object> predict(@RequestParam String crop) {

        double avg = generateData(crop).stream()
                .mapToDouble(p -> (Double) p.get("modal"))
                .average()
                .orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("predictedPrice", Math.round(avg * 1.05));
        return result;
    }

    @GetMapping("/weekly")
    public List<Map<String, Object>> weekly(@RequestParam String crop) {

        List<Map<String, Object>> list = new ArrayList<>();
        Random r = new Random();

        double base;

        switch (crop.toLowerCase()) {
            case "wheat":
                base = 2000;
                break;
            case "onion":
                base = 1300;
                break;
            case "tomato":
                base = 1000;
                break;
            default:
                base = 1500;
        }

        Calendar today = Calendar.getInstance();

        for (int i = 6; i >= 0; i--) {

            Calendar temp = (Calendar) today.clone();
            temp.add(Calendar.DATE, -i);

            int day = temp.get(Calendar.DAY_OF_MONTH);
            int month = temp.get(Calendar.MONTH) + 1;

            String date = day + "/" + month;

            Map<String, Object> data = new HashMap<>();
            data.put("day", date);
            data.put("price", Math.round(base + r.nextInt(200) - 100));

            list.add(data);
        }

        return list;
    }

    private List<Map<String, Object>> generateData(String crop) {

        List<Map<String, Object>> prices = new ArrayList<>();

        if (crop.equalsIgnoreCase("Wheat")) {
            prices.add(create("Pune", 2000));
            prices.add(create("Nashik", 1950));
            prices.add(create("Mumbai", 2100));
        }

        if (crop.equalsIgnoreCase("Onion")) {
            prices.add(create("Pune", 1300));
            prices.add(create("Solapur", 1200));
            prices.add(create("Nagpur", 1250));
        }

        if (crop.equalsIgnoreCase("Tomato")) {
            prices.add(create("Nashik", 1000));
            prices.add(create("Kolhapur", 1100));
            prices.add(create("Aurangabad", 950));
        }

        return prices;
    }

    private Map<String, Object> create(String market, double modal) {
        Map<String, Object> m = new HashMap<>();
        m.put("market", market);
        m.put("modal", modal);
        return m;
    }
}
