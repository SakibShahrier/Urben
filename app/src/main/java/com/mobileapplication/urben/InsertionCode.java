package com.mobileapplication.urben;

public class InsertionCode {
    /*
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("locations");
        for(int i = 0; i < 10; i++) {
            LocationProfile push = new LocationProfile(getResources().getStringArray(R.array.location_names)[i],
                    getResources().getStringArray(R.array.coordinates)[i], i);
            String key = databaseReference.push().getKey();

            databaseReference.child(key).setValue(push).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    */

    /*
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("locations");
        LocationProfile push = new LocationProfile(place_name, coordinate, position_number);
        String key = databaseReference.push().getKey();

        databaseReference.child(key).setValue(push).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    */
}
