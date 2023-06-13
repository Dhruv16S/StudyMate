# StudyMate

StudyMate is an Android application designed to enhance the learning experience for students and users alike. With StudyMate, users can effortlessly create, manage, and share notes, making it a valuable tool for studying and knowledge retention. The app offers a range of features, including Create Notes, View Notes, Question Answering Systems, and Share Notes.

The application leverages `Appwrite as a backend service` and utilises its features such as Auth, Database, Storage and Realtime to cater the intended functionalities.

The Create Notes feature empowers users to easily compose and organize their study materials. Users can input text directly, attach files, or even utilize Optical Character Recognition (OCR) to automatically extract text from images. This enables seamless note-taking and saves valuable time.

The Question Answering Systems feature utilizes a BERT (Bidirectional Encoder Representations from Transformers) model, a cutting-edge Natural Language Processing (NLP) technique. Users can ask questions related to their notes, and StudyMate leverages the power of AI and ML to provide accurate and relevant answers.

StudyMate caters to students and learners of all levels, providing them with a comprehensive platform to enhance their learning journey. By leveraging the capabilities of NLP, AI, and ML, StudyMate revolutionizes the way users engage with their study materials, making learning more efficient, interactive, and enjoyable.

## **Tech Stack**

- **Appwrite Cloud**
    - `Authentication:` Used to register new users and login the exisiting users to provide persistent app functionality.
    - `Database:` To store the notes and question cards created, and provide an interface to fetch them when needed.
    - `Storage:` To upload and retrieve user's files, and provide an interface to parse them using OCR tech.
    - `Realtime:` To share notes among users in realtime. 
- **Kotlin**
    - For developing the Android application and additional mobile features.
- **Google ML Kit**
    - To integrate and develop the OCR functionality of the application through the [Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition/v2) module.
- **Tensorflow**
    - Used to implement the [BERT Large Language Model](https://www.tensorflow.org/lite/examples/bert_qa/overview) for the Question Answering System through Tensorflow Lite.
- **[Filepicker](https://github.com/Atwa/filepicker)**
    - To select, read and upload files from a user's android device.
- **[Ramtion](https://github.com/Ramotion/circle-menu-android)**
    - To develop the circular menu bar used in the application.
- **Python and Jupyter Notebook**
    - To finetune the BERT Large Language Model.

To clone the application run the following command

`

git clone https://github.com/Dhruv16S/StudyMate.git

`

After cloning if there are any issues, try setting a different JAVA_HOME path and perform a Gradle sync in Android Studio.

Watch the application demo/tutorial here [StudyMate Application Demo](https://www.youtube.com/watch?v=-ud4fJUaPTk)
