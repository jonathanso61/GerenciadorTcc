package com.ifmg.gerenciadordetarefas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var googleSignInClient: GoogleSignInClient
    private val REQUEST_ADD_TASK = 1
    private val REQUEST_CODE_SIGN_IN = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskAdapter = TaskAdapter()

        val recyclerViewTasks = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = taskAdapter

        fabAddTask.setOnClickListener {
            startActivityForResult(Intent(this, AddTaskActivity::class.java), REQUEST_ADD_TASK)
        }

        taskAdapter.setOnEditClickListener { task ->
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra("taskId", task.id)
            startActivityForResult(intent, REQUEST_ADD_TASK)
        }

        taskAdapter.setOnDeleteClickListener { task ->
            TaskDBHelper(this).deleteTask(task.id)
            updateTaskList()
        }

        updateTaskList()
        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.google_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            fetchGmailMessages(account)
        } else {
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
        }
    }

    private fun fetchGmailMessages(account: GoogleSignInAccount) {
        val accessToken = account.idToken ?: run {
            Log.e("Gmail", "Erro ao obter o token de acesso")
            return
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()

        val request = Request.Builder()
            .url("https://gmail.googleapis.com/gmail/v1/users/me/messages")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        Thread {
            try {
                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val messages = Gson().fromJson(body, GmailMessageResponse::class.java)

                    messages?.messages?.forEach { message ->
                        Log.d("Gmail", "Message ID: ${message.id}")
                    }
                } else {
                    Log.e("Gmail", "Erro na requisição: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("Gmail", "Erro ao obter mensagens", e)
            }
        }.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ADD_TASK && resultCode == Activity.RESULT_OK) {
            val taskAdded = data?.getBooleanExtra("taskAdded", false) ?: false
            if (taskAdded) {
                updateTaskList()
            }
        } else if (requestCode == REQUEST_CODE_SIGN_IN) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = try {
                accountTask.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                null
            }

            if (account != null) {
                fetchGmailMessages(account)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateTaskList()
    }

    private fun updateTaskList() {
        val tasks = TaskDBHelper(this).getAllTasks()

        if (tasks.isEmpty()) {
            // Exibir mensagem "Você não tem nenhuma tarefa"
        } else {
            taskAdapter.setTasks(tasks)
        }
    }
}

data class GmailMessageResponse(val messages: List<GmailMessage>?)
data class GmailMessage(val id: String, val threadId: String)
