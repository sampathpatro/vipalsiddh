package com.sampathpatro.vipalsiddh

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.location.*
import com.google.android.play.core.review.ReviewManagerFactory
import com.sampathpatro.vipalsiddh.utils.LocationUtils
import kotlinx.android.synthetic.main.activity_main.*


val diseasesNames1 = arrayListOf(
    "Acute Myeloid Leukaemia",
    "Attention Deficit Hyperactivity Disorder (ADHD)",
    "Anal Cancer",
    "Anxiety",
    "Asbestosis",
    "Asthma",
    "Bipolar Disorder",
    "Blood Poisoning",
    "Breast Cancer",
    "Bronchitis",
    "Cellulitis",
    "Cervical Cancer",
    "Chicken Pox",
    "Cold Sore",
    "Common Cold",
    "Conjunctivitis",
    "Constipation",
    "COVID-19",
    "Croup",
    "Dehydration",
    "Depression",
    "Diabetes",
    "Diarrhoea",
    "Ebola",
    "Fever",
    "Flu",
    "Food Poisoning",
    "Gallbladder Cancer",
    "Gallstones",
    "Gastoenteritis",
    "Heart Problems",
    "Hepatitis A",
    "Hepatitis B",
    "Hepatitis C",
    "Herpes",
    "HIV/AIDS",
    "Indigestion",
    "Kidney Stones",
    "Malaria",
    "Malnutrition",
    "Measles",
    "Migraine",
    "Norovirus",
    "Obesity",
    "Obsessive Compulsive Disorder",
    "Penile Cancer",
    "Pneumonia",
    "Prostate Cancer",
    "Sinusitis",
    "Small Pox",
    "Sunburn",
    "Tuberculosis",
    "Vaginal Cancer",
    "Warts",
    "Yellow Fever",
    "Stress"
)
val diseasesList = ArrayList<Disease>()

class MainActivity : AppCompatActivity() {

    private var latitude = 0.toDouble()
    private var longitude = 0.toDouble()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private lateinit var locationManager: LocationManager
    private var gps_enabled = false

    private lateinit var lastLocation: Location


    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        val appUsed = sharedPreferences.getBoolean("AppUsed", false)

        val appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            dark_btn.setImageResource(R.drawable.light_mode_white_18dp)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            dark_btn.setImageResource(R.drawable.dark_mode_black_18dp)
        }

        dark_btn.setOnClickListener(View.OnClickListener {
            if(isNightModeOn){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()

                dark_btn.setImageResource(R.drawable.light_mode_white_18dp)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()

                dark_btn.setImageResource(R.drawable.dark_mode_black_18dp)
            }
        })


        val manager = ReviewManagerFactory.create(this)

        if (appUsed){
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = request.result
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                } else {
                    Toast.makeText(this, "Error: ${request.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        disease_grid.setOnClickListener {
            startActivity(Intent(this, DiseaseListActivity::class.java))
        }

        report_grid.setOnClickListener{
            startActivity(Intent(this, ReportBugActivity::class.java))
        }

        terms_grid.setOnClickListener{
            startActivity(Intent(this, PdfViewerActivity::class.java))
        }

        aboutDev_grid.setOnClickListener {
            startActivity(Intent(this, AboutMeActivity::class.java))
        }


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (gps_enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (LocationUtils.checkLocationPermission(this)) {
                    buildLocationRequest()
                    locationRequest = LocationUtils.buildLocationCallback()

                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(this)
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest, locationCallback,
                        Looper.myLooper()
                    )
                }

            } else {
                buildLocationRequest()
                locationRequest = LocationUtils.buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback,
                    Looper.myLooper()
                )
            }
        }

        btn_nearbyHosp.setOnClickListener {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!gps_enabled) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.location_request)
                builder.setIcon(R.drawable.ic_logo)
                builder.setMessage("Location services are required to show hospitals nearby.")
                builder.setPositiveButton(R.string.enable_location) { dialogInterface, which ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                builder.setNegativeButton(R.string.cancel) { dialogInterface, which ->
                    Toast.makeText(
                        baseContext,
                        "Location permission not allowed.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("LAT", latitude)
                intent.putExtra("LNG", longitude)
                startActivity(intent)
            }
        }

        btn_selfDiag.setOnClickListener {
            val intent = Intent(this, SymptomsActivity::class.java)
            startActivity(intent)
        }

        for (diseaseName in diseasesNames1) {
            when (diseaseName) {
                "Acute Myeloid Leukaemia" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A type of cancer of the blood and bone marrow with excess immature white blood cells. AML progresses rapidly, with myeloid cells interfering with the production of normal white blood cells, red blood cells and  platelets.\n",
                            arrayListOf(
                                "Fever", "Fatigue", "Bruising", "Bleeding"
                            ),
                            4,
                            3,
                            0
                        )
                    )

                "Attention Deficit Hyperactivity Disorder (ADHD)" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A chronic condition including attention difficulty, hyperactivity and impulsiveness. ADHD often begins in childhood and can persist into adulthood. It may contribute to low self-esteem, troubled relationships and difficulty at school or work.",
                            arrayListOf(
                                "Depression", "Mood Swings", "Hyperactivity", "Aggression"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Anal Cancer" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Anal cancer is an uncommon type of cancer that occurs in the anal canal. The anal canal is a short tube at the end of your rectum through which stool leaves your body. Anal cancer can cause signs and symptoms such as rectal bleeding and anal pain.",
                            arrayListOf(
                                "Anal Bleeding", "Anal Pain", "Anal Itching"
                            ),
                            4,
                            2,
                            0
                        )
                    )

                "Anxiety" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Anal cancer is an uncommon type of cancer that occurs in the anal canal. The anal canal is a short tube at the end of your rectum through which stool leaves your body. Anal cancer can cause signs and symptoms such as rectal bleeding and anal pain.",
                            arrayListOf(
                                "Fatigue", "Sweating", "Insomnia (Lack of Sleep)", "Nausea (Vomiting)"
                            ),
                            1,
                            5,
                            0
                        )
                    )

                "Asbestosis" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Asbestosis is long term inflammation and scarring of the lungs due to asbestos fibers. Asbestosis is caused by breathing in asbestos fibers. Complications may include lung cancer, mesothelioma, and pulmonary heart disease.",
                            arrayListOf(
                                "Fatigue", "Chest Pain", "Cough", "Difficulty in Breathing"
                            ),
                            3,
                            2,
                            0
                        )
                    )

                "Asthma" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A condition in which a person's airways become inflamed, narrow and swell and produce extra mucus, which makes it difficult to breathe. Asthma can be minor or it can interfere with daily activities. In some cases, it may lead to a life-threatening attack.",
                            arrayListOf(
                                "Difficulty in Breathing", "Chest Pain", "Cough"
                            ),
                            3,
                            5,
                            0
                        )
                    )

                "Bipolar Disorder" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A disorder associated with episodes of mood swings ranging from depressive lows to manic highs.The exact cause of bipolar disorder isn’t known, but a combination of genetics, environment and altered brain structure and chemistry may play a role.",
                            arrayListOf(
                                "Mood Swings",
                                "Hyperactivity",
                                "Aggression",
                                "Insomnia (Lack of Sleep)"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Blood Poisoning" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Blood poisoning is a serious infection. It occurs when bacteria are in the bloodstream.Sepsis occurs when chemicals released in the bloodstream to fight an infection trigger inflammation throughout the body. This can cause a cascade of changes that damage multiple organ systems, leading them to fail, sometimes even resulting in death.",
                            arrayListOf(
                                "Fever", "Difficulty in Breathing", "Low Blood Pressure"
                            ),
                            5,
                            4,
                            0
                        )
                    )

                "Breast Cancer" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A cancer that forms in the cells of the breasts. Breast cancer can occur in women and rarely in men. Its treatment depends on the stage of cancer. It may consist of chemotherapy, radiation, hormone therapy and surgery.",
                            arrayListOf(
                                "Lumps", "Nipple Bleeding", "Breast Deformation"
                            ),
                            4,
                            4,
                            0
                        )
                    )

                "Bronchitis" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Inflammation of the lining of bronchial tubes, which carry air to and from the lungs. Acute bronchitis is often caused by a viral respiratory infection and improves by itself.\n" +
                                    "Treatments usually includes soothing remedies to help with coughing, which may last weeks. Antibiotics are not usually recommended.",
                            arrayListOf(
                                "Chest Pain",
                                "Headache",
                                "Insomnia (Lack of Sleep)",
                                "Sore Throat",
                                "Fatigue",
                                "Runny Nose"
                            ),
                            1,
                            5,
                            0
                        )
                    )

                "Cellulitis" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A common and potentially serious bacterial skin infection. With cellulitis, the bacteria enter the skin.\n" +
                                    "Cellulitis may spread rapidly. Affected skin appears swollen and red and may be hot and tender.\n" +
                                    "Without treatment with an antibiotic, cellulitis can be life-threatening.",
                            arrayListOf(
                                "Fever", "Chills", "Pus", "Swelling"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "Cervical Cancer" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A malignant tumour of the cervix, the lowermost part of the uterus. A malignant tumour of the lower-most part of the uterus (womb) that can be prevented by PAP smear screening and a HPV vaccine. In some cases there may be no symptoms.\n" +
                                    "Treatments include surgery, radiation and chemotherapy.",
                            arrayListOf(
                                "Abnormal Menstruation", "Nausea (Vomiting)", "Weight Loss", "Vaginal Bleeding"
                            ),
                            3,
                            3,
                            0
                        )
                    )

                "Chicken Pox" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A highly contagious viral infection which causes an itchy, blister-like rash on the skin.Chickenpox is highly contagious to those who haven't had the disease or been vaccinated against it.\n" +
                                    " Chickenpox can be prevented by a vaccine. Treatment usually involves relieving symptoms, although high-risk groups may receive antiviral medication.",
                            arrayListOf(
                                "Blisters", "Ulcers", "Fatigue", "Fever", "Headache"
                            ),
                            3,
                            3,
                            0
                        )
                    )

                "Cold Sore" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Infection with the herpes simplex virus around the border of the lips.Oral herpes spreads through close personal contact, such as kissing.\n" +
                                    " Oral herpes causes tiny, fluid-filled lesions called cold sores or fever blisters, which can recur.\n" +
                                    " Medication can speed up healing and reduce recurrence.",
                            arrayListOf(
                                "Blisters", "Ulcers", "Rashes", "Sore Lip"
                            ),
                            1,
                            5,
                            0
                        )
                    )

                "Common Cold" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A common viral infection of the nose and throat.In contrast to the flu, a common cold can be caused by many different types of viruses. The condition is generally harmless and symptoms usually resolve within two weeks. Most people recover on their own within two weeks. Over-the-counter products and home remedies can help control symptoms.",
                            arrayListOf(
                                "Cold",
                                "Runny Nose",
                                "Cough",
                                "Chest Pain",
                                "Fever",
                                "Sinus",
                                "Headache",
                                "Chills",
                                "Sore Throat"
                            ),
                            1,
                            5,
                            0
                        )
                    )

                "Conjunctivitis" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Inflammation or infection of the outer membrane of the eyeball and the inner eyelid.Conjunctivitis, or pink eye, is an irritation or inflammation of the conjunctiva, which covers the white part of the eyeball. It can be caused by allergies or a bacterial or viral infection. Conjunctivitis can be extremely contagious and is spread by contact with eye secretions from someone who is infected.",
                            arrayListOf(
                                "Eye Pain", "Swollen Eye", "Redness in Eyes", "Sneezing"
                            ),
                            2,
                            4,
                            0
                        )
                    )

                "Constipation" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "When a person passes less than three bowel movements a week or has difficult bowel movements.Constipation can have causes that aren't due to underlying disease. Examples include dehydration, lack of dietary fibre, physical inactivity or medication side effects.",
                            arrayListOf(
                                "Stomach Ache", "Strain to Pass Stool", "Anal Pain", "Anal Bleeding"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "COVID-19" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "An infectious disease caused by a newly discovered coronavirus. Most people who fall sick with COVID-19 will experience mild to moderate symptoms and recover without special treatment.",
                            arrayListOf(
                                "Fever",
                                "Cough",
                                "Exhaustion",
                                "Difficulty in Breathing",
                                "Chest Pain"
                            ),
                            4,
                            5,
                            0
                        )
                    )

                "Croup" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "An upper airway infection that blocks breathing and has a distinctive barking cough.Croup generally occurs in children.\n" +
                                    " Most cases clear up with home care in three to five days. A doctor may prescribe a steroid for a persistent case. Rarely, a severe case may need hospital care.",
                            arrayListOf(
                                "Fever", "Cough", "Difficulty in Breathing"
                            ),
                            2,
                            4,
                            0
                        )
                    )

                "Dehydration" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A dangerous loss of body fluid caused by illness, sweating or inadequate intake. Dehydration can have causes that aren't due to underlying disease. Examples include heat, excessive activity, insufficient fluid consumption, excessive sweating or medication side effects.",
                            arrayListOf(
                                "Nausea (Vomiting)", "Dizziness", "Exhaustion", "Headache"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Depression" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A mental health disorder characterised by persistently depressed mood or loss of interest in activities, causing significant impairment in daily life.Possible causes include a combination of biological, psychological and social sources of distress. Increasingly, research suggests that these factors may cause changes in brain function, including altered activity of certain neural circuits in the brain.",
                            arrayListOf(
                                "Mood Swings",
                                "Hyperactivity",
                                "Aggression",
                                "Exhaustion",
                                "Fatigue",
                                "Insomnia (Lack of Sleep)"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Diabetes" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A group of diseases that result in too much sugar in the blood (high blood glucose). a metabolic disease that causes high blood sugar. The hormone insulin moves sugar from the blood into your cells to be stored or used for energy. With diabetes, your body either doesn't make enough insulin or can't effectively use the insulin it does make.",
                            arrayListOf(
                                "Increased Thirst", "Fatigue", "Weight Loss", "Blurred Vision"
                            ),
                            1,
                            5,
                            0
                        )
                    )

                "Diarrhoea" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Loose, watery stools that occur more frequently than usual. Diarrhoea is usually caused by a virus, or sometimes, contaminated food. Less frequently, it can be a sign of another disorder, such as inflammatory bowel disease or irritable bowel syndrome.Diarrhoea is usually caused by a virus, or sometimes, contaminated food. Less frequently, it can be a sign of another disorder, such as inflammatory bowel disease or irritable bowel syndrome.",
                            arrayListOf(
                                "Fever", "Stomach Ache", "Weakness", "Nausea (Vomiting)"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Ebola" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A virus that causes severe bleeding, organ failure and can lead to death.Humans may spread the virus to other humans through contact with bodily fluids such as blood.Initial symptoms include fever, headache, muscle pain and chills. Later, a person may experience internal bleeding resulting in vomiting or coughing blood.\n" +
                                    " Treatment is supportive hospital care.",
                            arrayListOf(
                                "Fever", "Headache", "Fatigue", "Nausea (Vomiting)", "Stomach Ache"
                            ),
                            5,
                            1,
                            0
                        )
                    )

                "Epilepsy" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A disorder in which nerve cell activity in the brain is disturbed, causing seizures.Epilepsy may occur as a result of a genetic disorder or an acquired brain injury, such as a trauma or stroke.\n" +
                                    " During a seizure, a person experiences abnormal behaviour, symptoms and sensations, sometimes including loss of consciousness.",
                            arrayListOf(
                                "Fatigue", "Anxiety", "Sweating", "Exhaustion"
                            ),
                            2,
                            4,
                            0
                        )
                    )

                "Fever" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A temporary increase in average body temperature of 98.6°F. It is a temporary increase in your body temperature, often due to an illness. Having a fever is a sign that something out of the ordinary is going on in your body. For an adult, a fever may be uncomfortable, but usually isn't a cause for concern unless it reaches 103 F (39.4 C) or higher.",
                            arrayListOf(
                                "Fever", "Chills", "Cough", "Headache", "Fatigue"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Flu" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A common viral infection that can be deadly, especially in high-risk groups.The flu attacks the lungs, nose and throat. Young children, older adults, pregnant women and people with chronic disease or weak immune systems are at high risk.The flu attacks the lungs, nose and throat. Young children, older adults, pregnant women and people with chronic disease or weak immune systems are at high risk.",
                            arrayListOf(
                                "Chills", "Cough", "Headache", "Fatigue", "Fever", "Weakness"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Food Poisoning" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Illness caused by food contaminated with bacteria, viruses, parasites or toxins. Infectious organisms or their toxins are the most common causes of food poisoning.Most food poisoning is mild and resolves without treatment. Ensuring adequate hydration is the most important aspect of treatment.\n",
                            arrayListOf(
                                "Nausea (Vomiting)", "Stomach Ache", "Fever", "Fatigue"
                            ),
                            3,
                            5,
                            0
                        )
                    )

                "Gallbladder Cancer" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Cancer that develops in the gallbladder, a small organ below the liver.The gallbladder's size and location make it easier for the cancer to grow undetected.\n" +
                                    " There may be no symptoms. If symptoms occur, they may include abdominal pain, bloating and fever.\n" +
                                    " Treatments include surgery, chemotherapy and radiation.",
                            arrayListOf(
                                "Stomach Ache", "Nausea (Vomiting)", "Fever", "Lumps", "Yellow Eyes"
                            ),
                            3,
                            2,
                            0
                        )
                    )

                "Gallstones" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A hardened deposit within the fluid in the gallbladder, a small organ under the liver. Gallstones are hardened deposits of digestive fluid.\n" +
                                    " Gallstones can vary in size and number and may or may not cause symptoms.\n" +
                                    " People who experience symptoms usually require gallbladder removal surgery. Gallstones that don't cause symptoms usually don't need treatment.",
                            arrayListOf(
                                "Stomach Ache", "Nausea (Vomiting)", "Fever"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Gastoenteritis" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "An intestinal infection marked by diarrhoea, cramps, nausea, vomiting and fever. Stomach flu is typically spread by contact with an infected person or through contaminated food or water.Avoiding contaminated food and water and washing hands can often help prevent infection. Rest and rehydration are the mainstays of treatment.",
                            arrayListOf(
                                "Stomach Ache",
                                "Nausea (Vomiting)",
                                "Fatigue",
                                "Chills",
                                "Weakness",
                                "Headache"
                            ),
                            1,
                            5,
                            0
                        )
                    )

                "Heart Problems" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A chronic condition in which the heart doesn't pump blood as well as it should.Heart failure can occur if the heart cannot pump (systolic) or fill (diastolic) adequately.Treatments can include eating less salt, limiting fluid intake and taking prescription medication. In some cases a defibrillator or pacemaker may be implanted.",
                            arrayListOf(
                                "Difficulty in Breathing", "Fatigue", "Chest Pain", "Weight Gain"
                            ),
                            4,
                            4,
                            0
                        )
                    )

                "Herpes" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A virus causing contagious sores, most often around the mouth or on the genitals. It can be in the form of infection with the herpes simplex virus around the border of the lips known as cold sores. Or a common sexually transmitted infection marked by genital pain and sores.",
                            arrayListOf(
                                "Blisters", "Sores", "Itching"
                            ),
                            2,
                            4,
                            0
                        )
                    )

                "Hepatitis A" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A highly contagious liver infection caused by the hepatitis A virus. Hepatitis A is preventable by vaccine. It spreads from contaminated food or water or contact with someone who is infected. The condition clears up on its own in one or two months. Rest and adequate hydration can help.",
                            arrayListOf(
                                "Fatigue", "Nausea (Vomiting)", "Stomach Ache", "Fever"
                            ),
                            4,
                            3,
                            0
                        )
                    )

                "Hepatitis B" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A serious liver infection caused by the hepatitis B virus that's easily preventable by a vaccine. This disease is most commonly spread by exposure to infected bodily fluids. The condition often clears up on its own. Chronic cases require medication and possibly a liver transplant.",
                            arrayListOf(
                                "Yellow Eyes", "Nausea (Vomiting)", "Fatigue"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "Hepatitis C" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "An infection caused by a virus that attacks the liver and leads to inflammation. The virus is spread by contact with contaminated blood; for example, from sharing needles or from unsterile tattoo equipment.Hepatitis C is treated with antiviral medication. In some people, newer medicines can eradicate the virus.",
                            arrayListOf(
                                "Fatigue",
                                "Nausea (Vomiting)",
                                "Yellow Eyes",
                                "Stomach Ache",
                                "Bleeding",
                                "Fever",
                                "Weight Loss",
                                "Depression"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "HIV/AIDS" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "HIV causes AIDS and interferes with the body's ability to fight infections.\n" +
                                    "The virus can be transmitted through contact with infected blood, semen or vaginal fluids.\n" +
                                    "Within a few weeks of HIV infection, flu-like symptoms such as fever, sore throat and fatigue can occur. Then the disease is usually asymptomatic until it progresses to AIDS. ",
                            arrayListOf(
                                "Fever",
                                "Fatigue",
                                "Weight Loss",
                                "Stomach Ache",
                                "Bleeding",
                                "Fever",
                                "Weight Loss",
                                "Sweating"
                            ),
                            4,
                            4,
                            0
                        )
                    )

                "Indigestion" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Upper abdominal discomfort, described as burning sensation, bloating or gassiness, Nausea (Vomiting) or feeling full too quickly after starting to eat. Indigestion can have causes that aren't due to underlying disease. Examples include eating too much, drinking too much, food intolerance or taking pills on an empty stomach.",
                            arrayListOf(
                                "Stomach Ache", "Nausea (Vomiting)", "Gas"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Kidney Stones" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A small, hard deposit that forms in the kidneys and is often painful when passed.Kidney stones are hard deposits of minerals and acid salts that stick together in concentrated urine. They can be painful when passing through the urinary tract, but usually don't cause permanent damage.Treatment includes pain relievers and drinking lots of water to help pass the stone. Medical procedures may be required to remove or break up larger stones.",
                            arrayListOf(
                                "Back Pain", "Stomach Ache", "Nausea (Vomiting)", "Sweating"
                            ),
                            2,
                            4,
                            0
                        )
                    )

                "Malaria" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A disease caused by a plasmodium parasite, transmitted by the bite of infected mosquitoes.The severity of malaria varies based on the species of plasmodium.People travelling to areas where malaria is common typically take protective drugs before, during and after their trip. Treatment includes antimalarial drugs.",
                            arrayListOf(
                                "Fever",
                                "Cold",
                                "Nausea (Vomiting)",
                                "Sweating",
                                "Stomach Ache",
                                "Fatigue",
                                "Headache"
                            ),
                            3,
                            3,
                            0
                        )
                    )

                "Malnutrition" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Lack of sufficient nutrients in the body.Malnutrition occurs when the body doesn't get enough nutrients. Causes include a poor diet, digestive conditions or another disease.Treatment must address any underlying conditions and replace missing nutrients.",
                            arrayListOf(
                                "Fatigue", "Dizziness", "Weight Loss"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "Measles" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A viral infection that's serious for small children but is easily preventable by a vaccine.The disease spreads through the air by respiratory droplets produced from coughing or sneezing.\n" +
                                    "Measles symptoms don't appear until 10 to 14 days after exposure.There's no treatment to get rid of an established measles infection, but over-the-counter fever reducers or vitamin A may help with symptoms.",
                            arrayListOf(
                                "Cough", "Runny Nose", "Fever", "Rashes"
                            ),
                            4,
                            1,
                            0
                        )
                    )

                "Migraine" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A headache of varying intensity, often accompanied by nausea and sensitivity to light and sound.Migraine headaches are sometimes preceded by warning symptoms. Triggers include hormonal changes, certain food and drink, stress and exercise.Preventive and pain-relieving medication can help manage migraine headaches.",
                            arrayListOf(
                                "Headache", "Nausea (Vomiting)"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Norovirus" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Norovirus, sometimes referred to as the winter vomiting bug, is the most common cause of gastroenteritis. Infection is characterized by non-bloody diarrhea, vomiting, and stomach pain. Fever or headaches may also occur.",
                            arrayListOf(
                                "Nausea (Vomiting)", "Fever", "Stomach Ache", "Weakness"
                            ),
                            3,
                            3,
                            0
                        )
                    )

                "Obesity" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A disorder involving excessive body fat that increases the risk of health problems.Obesity often results from taking in more calories than are burned by exercise and normal daily activities.\n" +
                                    "Obesity occurs when a person's body mass index is 25 or greater. The excessive body fat increases the risk of serious health problems.\n" +
                                    "The mainstay of treatment is lifestyle changes such as diet and exercise.",
                            arrayListOf(
                                "Fatigue", "Over Weight", "Stomach Ache"
                            ),
                            3,
                            5,
                            0
                        )
                    )

                "Obsessive Compulsive Disorder" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Excessive thoughts (obsessions) that lead to repetitive behaviours (compulsions).Obsessive-compulsive disorder is characterised by unreasonable thoughts and fears (obsessions) that lead to compulsive behaviours.\n" +
                                    "OCD often centres on themes such as a fear of germs or the need to arrange objects in a specific manner. Symptoms usually begin gradually and vary throughout life.\n" +
                                    "Treatment includes talk therapy, medication or both.",
                            arrayListOf(
                                "Anxiety", "Hyperactivity", "Depression", "Mood Swings"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "Penile Cancer" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A disease in which cancer cells form in the tissues of the penis.Human papillomavirus (HPV) infection may increase the risk of penile cancer. Condom use and the HPV vaccine can help prevent infection with HPV.Penile cancer may begin as a blister on the foreskin, head or shaft of the penis. It may become a wart-like growth that discharges blood or foul-smelling liquid.\n" +
                                    "Surgery is the most common treatment for all stages of penile cancer. Other options include radiation and chemotherapy.",
                            arrayListOf(
                                "Lumps", "Ulcers", "Blisters", "Penile Pain"
                            ),
                            4,
                            2,
                            0
                        )
                    )

                "Pneumonia" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Infection that inflames air sacs in one or both lungs, which may fill with fluid.With pneumonia, the air sacs may fill with fluid or pus. The infection can be life-threatening to anyone, but particularly to infants, children and people over 65.Antibiotics can treat many forms of pneumonia. Some forms of pneumonia can be prevented by vaccines.",
                            arrayListOf(
                                "Fever", "Chills", "Chest Pain", "Cough", "Difficulty in Breathing"
                            ),
                            2,
                            5,
                            0
                        )
                    )

                "Prostate Cancer" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A cancer in a man's prostate, a small walnut-sized gland that produces seminal fluid.A man's prostate produces the seminal fluid that nourishes and transports sperm.Some types of prostate cancer grow slowly. In some of these cases, monitoring is recommended. Other types are aggressive and require radiation, surgery, hormone therapy, chemotherapy or other treatments.",
                            arrayListOf(
                                "Difficulty in Urination", "Penile Pain", "Frequent Urination"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "Sinusitis" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A condition in which the cavities around the nasal passages become inflamed.Acute sinusitis can be triggered by a cold or allergies and may resolve on its own. Chronic sinusitis lasts up to eight weeks and may be caused by an infection or growths.Acute sinusitis usually doesn't require any treatment beyond symptomatic relief with pain medication, nasal decongestants and nasal saline rinses. Chronic sinusitis may require antibiotics.",
                            arrayListOf(
                                "Headache",
                                "Runny Nose",
                                "Fatigue",
                                "Fever",
                                "Insomnia (Lack of Sleep)"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "Small Pox" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "An eradicated virus that used to be contagious, disfiguring and often deadly.Naturally occurring smallpox was destroyed worldwide by 1980.\n" +
                                    "In addition to flu-like symptoms, patients also experience a rash that appears first on the face, hands and forearms and then later appears on the torso.There's no treatment or cure for smallpox. A vaccine can prevent it, but the vaccine's side effect risk is too high to justify routine vaccination for people at low risk of exposure to the virus.\n",
                            arrayListOf(
                                "Blisters", "Fever", "Runny Nose", "Headache", "Fever", "Cough"
                            ),
                            3,
                            1,
                            0
                        )
                    )

                "Sunburn" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A type of skin burn resulting from too much exposure to sunlight or sunlamps.Repeated exposure increases the risk of other conditions such as wrinkles, dark spots and skin cancer.\n" +
                                    "Symptoms include red, painful, itchy skin that's hot to the touch. Skin may also blister.\n" +
                                    "Treatment includes pain relievers and creams to relieve itching.\n",
                            arrayListOf(
                                "Blisters", "Itchiness", "Red Spots"
                            ),
                            1,
                            5,
                            0
                        )
                    )

                "Tuberculosis" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A potentially serious infectious bacterial disease that mainly affects the lungs. The bacteria that cause TB are spread when an infected person coughs or sneezes.Treatment isn't always required for those without symptoms. Patients with active symptoms will require a long course of treatment involving multiple antibiotics.\n",
                            arrayListOf(
                                "Cough",
                                "Weight Loss",
                                "Fever",
                                "Fatigue",
                                "Difficulty in Breathing",
                                "Chest Pain"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "Vaginal Cancer" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Vaginal cancer is a rare cancer that occurs in your vagina — the muscular tube that connects your uterus with your outer genitals. Vaginal cancer most commonly occurs in the cells that line the surface of your vagina, which is sometimes called the birth canal.\n",
                            arrayListOf(
                                "Vaginal Bleeding",
                                "Lumps",
                                "Constipation",
                                "Difficulty in Urination"
                            ),
                            3,
                            4,
                            0
                        )
                    )

                "Warts" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A small, fleshy bump on the skin or mucous membrane caused by human papillomavirus. Warts are caused by various strains of human papillomaviruses. Different strains may cause warts in different parts of the body. Warts can be spread from one location on the body to another or from person to person by contact with the wart.Treatment may include topical medication and removal through medical procedures.\n",
                            arrayListOf(
                                "Itchiness", "Lumps", "Blisters"
                            ),
                            3,
                            5,
                            0
                        )
                    )

                "Yellow Fever" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "A viral infection spread by a particular species of mosquito. Yellow fever is spread by a species of mosquito common to areas of Africa and South America. Vaccination is recommended before travelling to certain areas.Yellow fever is spread by a species of mosquito common to areas of Africa and South America. Vaccination is recommended before travelling to certain areas.\n",
                            arrayListOf(
                                "Fever",
                                "Headache",
                                "Nausea (Vomiting)",
                                "Stomach Ache",
                                "Fatigue",
                                "Chills",
                                "Bleeding"
                            ),
                            2,
                            1,
                            0
                        )
                    )

                "Stress" ->
                    diseasesList.add(
                        Disease(
                            diseaseName,
                            "Stress is not a disease. Stress is a situation that triggers a particular biological response. When you perceive a threat or a major challenge, chemicals and hormones surge throughout your body.\n" +
                                    "Stress triggers your fight-or-flight response in order to fight the stressor or run away from it. Typically, after the response occurs, your body should relax. Too much constant stress can have negative effects on your long-term health.",
                            arrayListOf(
                                "Stress", "Anxiety", "Headache", "Fatigue", "Depression"
                            ),
                            1,
                            4,
                            0
                        )
                    )


            }
        }

        view_tutorial.setOnClickListener {
            startActivity(Intent(this, TutorialActivity::class.java))
        }

        for (dis in diseasesList) {
            for (symp in dis.diseaseSymptoms) {
                symptomsList.add(symp)
            }
        }

    }

    private fun buildLocationRequest() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                lastLocation = p0!!.locations[p0.locations.size - 1]

                latitude = lastLocation.latitude
                longitude = lastLocation.longitude
            }
        }
    }



}