package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.assignment1.databinding.ActivityMainBinding;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    //Variable for Logcat (for debug use)
    private final static String TAG = "Test_Output";

    //Declare and initiate the value of Variables (Setup Initial Value)
    int num_of_people = 0;
    String hungriness = "light";
    float slice_per_pizza = 6f;
    float size_of_pizza_cost = 2f;
//    String type_of_pizza = "Cheese(RM8.50)";
//    String total_needed = "0";
//    String total_cost = "RM 0.00";

    ActivityMainBinding binding = null;

    //Create a model object
    PizzaPrice viewModel = null;

    ////////////////////////////////////////
    //Main Method                         //
    ////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        //Instantiate the view model object
        viewModel = new ViewModelProvider(this).get(PizzaPrice.class);

        //Call eventManagement() method
        eventHandler();
    }

    ////////////////////////////////////////
    //Method for Pause an App Event       //
    ////////////////////////////////////////
    @Override
    protected void onPause(){
        super.onPause();

        //Get data from activity
        String pause_total_needed = binding.textTotalPizzas.getText().toString();
        String pause_total_cost = binding.textTotalCost.getText().toString();

        //Save data to viewModel
        viewModel.setTotal_needed(pause_total_needed);
        viewModel.setTotal_cost(pause_total_cost);
    }

    ////////////////////////////////////////
    //Method for Resume an App Event      //
    ////////////////////////////////////////
    @Override
    protected void onResume(){
        super.onResume();

        //Load data from viewModel
        binding.textTotalPizzas.setText(viewModel.getTotal_needed());
        binding.textTotalCost.setText(viewModel.getTotal_cost());

        //Text_total_pizzas value should be "0" after the app start (Prevent to show the output when user first comes into app)
        if(binding.textTotalPizzas.getText().toString() != "0") {
            //Set output text visible after button event execute
            binding.textTotalNeededDisplay.setVisibility(View.VISIBLE);
            binding.textTotalCostDisplay.setVisibility(View.VISIBLE);
            binding.textTotalPizzas.setVisibility(View.VISIBLE);
            binding.textTotalCost.setVisibility(View.VISIBLE);
        }
    }

    ////////////////////////////////////////
    //Method for Event Handling           //
    ////////////////////////////////////////
    public void eventHandler(){
        //Set event handler for radio_group_hungryness
        binding.radioGroupHungryness.setOnCheckedChangeListener((group, checkedId) -> {
            changeRadioData(checkedId);
        });

        //Set event handler for radio_group_slice_num
        binding.radioGroupSliceNum.setOnCheckedChangeListener((group, checkedId) -> {
            changeRadioData(checkedId);
        });

        //Set event handler for radio_group_size_pizza
        binding.radioGroupSizePizza.setOnCheckedChangeListener((group, checkedId) ->{
            changeRadioData(checkedId);
        });

        //Set event handler for button_calculate
        binding.buttonCalculate.setOnClickListener(v -> {
            try{
                int loc_numberOfPeople = Integer.parseInt(binding.textPeopleNum.getText().toString());//NumberFormatException
                String loc_hungriness = hungriness;
                float loc_slice_per_pizza = slice_per_pizza;
                float loc_size_of_pizza_cost = size_of_pizza_cost;
                //String loc_pizza_type = binding.spinnerType.getSelectedItem().toString();
                int loc_pizza_type = binding.spinnerType.getSelectedItemPosition();

                if(loc_numberOfPeople <= 0){
                    Toast.makeText(MainActivity.this, "Number of People must not less than 1.", Toast.LENGTH_SHORT).show();
                    return;
                }

//                Log.d(TAG, "Number of People: " + loc_numberOfPeople);
//                Log.d(TAG, "Hungriness: " + loc_hungriness);
//                Log.d(TAG, "Slice per Pizza: " + loc_slice_per_pizza);
//                Log.d(TAG, "Pizza Type " + loc_pizza_type);

                int slice_per_person = levelOfHungriness(loc_hungriness);
                float pizza_type_price = pizzaTypePrice(loc_pizza_type);

                //Calculate how many people need how many pizzas
                int val_total_needed = totalPizzasCalculation(
                        loc_numberOfPeople,
                        loc_slice_per_pizza,
                        slice_per_person,
                        loc_size_of_pizza_cost);

                //Calculate total price
                String loc_total_cost = totalPriceCalculation(pizza_type_price, val_total_needed, loc_size_of_pizza_cost);

                //Convert int and float to String

                binding.textTotalPizzas.setText(String.valueOf(val_total_needed));
                binding.textTotalCost.setText("RM" + loc_total_cost);

                //Set output text visible after button event execute
                binding.textTotalNeededDisplay.setVisibility(View.VISIBLE);
                binding.textTotalCostDisplay.setVisibility(View.VISIBLE);
                binding.textTotalPizzas.setVisibility(View.VISIBLE);
                binding.textTotalCost.setVisibility(View.VISIBLE);
            }
            catch(NumberFormatException ex){
                Toast.makeText(MainActivity.this, "Text input must not be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ///////////////////////////////////////////////
    //Method for Total Pizzas Needed Calculation //
    ///////////////////////////////////////////////
    public int totalPizzasCalculation(int numberOfPeople, float slice_per_pizza, int slice_per_person, float size_of_pizza_cost){
        float multiplier = 1f;

        if(size_of_pizza_cost == 1f){
            multiplier = 1.5f;
        }
        else if(size_of_pizza_cost == 2f){
            multiplier = 1f;
        }
        else if(size_of_pizza_cost == 2.5f){
            multiplier = 0.5f;
        }

        float loc_total_needed = (numberOfPeople * (slice_per_person * multiplier))/slice_per_pizza;

        if(loc_total_needed < 1f){
            loc_total_needed = 1f;
        }

        if(loc_total_needed % 1 != 0){
            loc_total_needed = loc_total_needed + 1;
        }

        return (int) loc_total_needed; //Math.round(loc_total_needed);
    }

    //////////////////////////////////////////////
    //Method for Total Price Calculation        //
    //////////////////////////////////////////////
    public String totalPriceCalculation(float pizza_type_price, int val_total_needed, float loc_size_of_pizza_cost){
        float val_total_cost = (pizza_type_price + loc_size_of_pizza_cost) * (float)val_total_needed;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        //Float formatted_val_total_cost = decimalFormat.format(val_total_cost);

        return String.valueOf(decimalFormat.format(val_total_cost));
    }

    ////////////////////////////////////////
    //Data for Radio Group Method         //
    ////////////////////////////////////////
    public void changeRadioData(int checkedId){
        switch(checkedId){
            //Radio group for radio_group_hungryness
            case R.id.radio_light:
                hungriness = "light";
                //Log.d(TAG, "light radio selected");
                //Log.d(TAG, "Hungriness: " + hungriness);
                break;
            case R.id.radio_medium:
                hungriness = "medium";
                //Log.d(TAG, "medium radio selected");
                //Log.d(TAG, "Hungriness: " + hungriness);
                break;
            case R.id.radio_ravenous:
                hungriness = "ravenous";
                //Log.d(TAG, "ravenous radio selected");
                //Log.d(TAG, "Hungriness: " + hungriness);
                break;

            //Radio group for radio_group_slice_num
            case R.id.radio_4_slice:
                //Log.d(TAG, "4 radio selected");
                slice_per_pizza = 4f;
                break;
            case R.id.radio_6_slice:
                //Log.d(TAG, "6 radio selected");
                slice_per_pizza = 6f;
                break;
            case R.id.radio_8_slice:
                //Log.d(TAG, "8 radio selected");
                slice_per_pizza = 8f;
                break;

            //Radio group for radio_group_size_pizza
            case R.id.radio_personal:
                size_of_pizza_cost = 1.0f; //Cost for small size pizza
                break;
            case R.id.radio_regular:
                size_of_pizza_cost = 2.0f; //Cost for regular size pizza
                break;
            case R.id.radio_large:
                size_of_pizza_cost = 2.5f; //Cost for large size pizza
                break;

            default:
                Log.d(TAG, "Error! Invalid Radio ID");
                break;
        }
    }

    //////////////////////////////////////////////
    //Method for Hungriness                     //
    //////////////////////////////////////////////
    public int levelOfHungriness(String hungriness){
        switch(hungriness){
            case "light":
                return 1;
            case "medium":
                return 2;
            case "ravenous":
                return 4;
            default:
                Log.d(TAG, "Error! Invalid Level of Hungriness");
                return 0;
        }
    }

    //////////////////////////////////////////////
    //Method for Categorize Price of Pizza Type //
    //////////////////////////////////////////////
    public float pizzaTypePrice(int pizza_type){
        switch(pizza_type){
            case 0:
                return 8.5f; //RM8.50

            case 1:
                return 7.6f; //RM7.60

            case 2:
                return 10.2f; //RM10.20

            case 3:
                return 8.2f; //RM8.20

            case 4:
                return 6.9f; //RM6.90

            case 5:
                return 2.4f; //RM2.40

            case 6:
                return 3.2f; //RM3.20

            case 7:
                return 9.5f; //RM9.50

            case 8:
                return 4.1f; //RM4.10

            case 9:
                return 13.7f; //RM13.70

            default:
                Log.d(TAG, "Error! Invalid Spinner ID");
                return 0.0f; //RM0.00
        }
    }
}