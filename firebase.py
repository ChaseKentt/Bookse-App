import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from google.cloud import firestore

# authenticate and query Firebase Admin SDK
certJSON = "csce4901-firebase-adminsdk-vki7b-fe150de77a.json"
cred = credentials.Certificate(certJSON)
firebase_admin.initialize_app(cred, {'databaseURL': 'https://csce4901.firebaseio.com/book'})
ref = db.reference('restricted_access/secret_document')
print(ref.get)

# authenticate and query Firestore Know NOthings Bookse database
fs = firestore.Client.from_service_account_json('KnowNothings-3a22d35c166c.json')
books = fs.collection(u'book').stream()
for book in books:
    print(u'BookID:{} => {}'.format(book.id, book.to_dict()))

