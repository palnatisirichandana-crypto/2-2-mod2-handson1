package project.mod2_handson1

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var formContainer: LinearLayout
    private lateinit var btnSubmit: Button
    private lateinit var tvResult: TextView

    // Store all dynamic views here
    private val formViews = HashMap<String, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Linking XML Views
        formContainer = findViewById(R.id.formContainer)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvResult = findViewById(R.id.tvResult)

        // Load Dynamic Form
        loadFormFromJson()

        // Submit Button Click
        btnSubmit.setOnClickListener {
            submitForm()
        }
    }

    // ✅ Load JSON and Generate Dynamic Form
    private fun loadFormFromJson() {
        try {
            val jsonString = loadJSONFromAssets()
            val jsonObject = JSONObject(jsonString)

            val fields: JSONArray = jsonObject.getJSONArray("fields")

            for (i in 0 until fields.length()) {

                val field = fields.getJSONObject(i)

                val type = field.getString("type")
                val label = field.getString("label")
                val id = field.getString("id")

                // Label TextView
                val textView = TextView(this)
                textView.text = label
                textView.textSize = 18f
                formContainer.addView(textView)

                // Create Dynamic Fields
                when (type) {

                    // ✅ Text Input Field
                    "text" -> {
                        val editText = EditText(this)
                        editText.hint = label
                        formContainer.addView(editText)

                        formViews[id] = editText
                    }

                    // ✅ Dropdown Field
                    "dropdown" -> {
                        val spinner = Spinner(this)

                        val optionsArray = field.getJSONArray("options")
                        val options = ArrayList<String>()

                        for (j in 0 until optionsArray.length()) {
                            options.add(optionsArray.getString(j))
                        }

                        val adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            options
                        )

                        spinner.adapter = adapter
                        formContainer.addView(spinner)

                        formViews[id] = spinner
                    }

                    // ✅ Checkbox Field
                    "checkbox" -> {
                        val checkBox = CheckBox(this)
                        checkBox.text = label
                        formContainer.addView(checkBox)

                        formViews[id] = checkBox
                    }
                }

                // Space Between Fields
                val space = Space(this)
                space.minimumHeight = 25
                formContainer.addView(space)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ✅ Submit Form and Display Output in TextView
    private fun submitForm() {

        val result = StringBuilder()
        result.append("✅ Submitted Form Data:\n\n")

        for ((key, view) in formViews) {

            when (view) {

                // Text Input Value
                is EditText -> {
                    result.append("$key : ${view.text}\n")
                }

                // Dropdown Value
                is Spinner -> {
                    result.append("$key : ${view.selectedItem}\n")
                }

                // Checkbox Value
                is CheckBox -> {
                    val checked = if (view.isChecked) "Accepted" else "Not Accepted"
                    result.append("$key : $checked\n")
                }
            }
        }

        // ✅ Show Result on Screen
        tvResult.text = result.toString()
    }

    // ✅ Read JSON File from Assets Folder
    private fun loadJSONFromAssets(): String {
        return try {
            val inputStream = assets.open("form_config.json")
            val size = inputStream.available()

            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            String(buffer, Charsets.UTF_8)

        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
