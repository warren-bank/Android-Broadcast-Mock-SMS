#### [Broadcast Mock SMS](https://github.com/warren-bank/Android-Broadcast-Mock-SMS)

Android app that broadcasts a mock SMS to mimic receiving a new text message.

#### Overview:

* this is a tool useful to Android developers
  * helps to test broadcast receivers that monitor and respond to inbound SMS messages

#### Screenshot:

![1-compose-sender](./screenshots/1-compose-sender.png)
![2-compose-message](./screenshots/2-compose-message.png)
![3-sent](./screenshots/3-sent.png)

#### Notes:

* minimum supported version of Android:
  * Android 1.1 (API 2)

#### Caveats:

* YMMV
  * it may work on some devices, but not others
  * it may work on some versions of Android, but not others
    * according to [this discussion](https://stackoverflow.com/questions/16143186/can-i-send-sms-received-intent-in-android-4-1-2), beginning with Android 4.1.2 JB, this code will only work when run as a system app
      * root is required
      * [Link2SD](https://play.google.com/store/apps/details?id=com.buak.Link2SD) is a free app that automates converting an app between user and system (as well as lots of other useful things)
    * according to my (very limited) testing, the previous assertion appears to be untrue
  * on test device &#x23;1 (4.4.2 KK):
    * broadcasting the `SMS_RECEIVED` Intent:
      * initially, not working
        * logcat: a Security Exception was being thrown by another unrelated app
        * started working after "Visual Voicemail" was frozen
    * starting the `SmsReceiverService`:
      * not working:
        * logcat: _Unable to start service Intent ... not found_
        * does not throw any Exception
        * same behavior when the app is installed as user or system
      * __TO DO:__ look into the cause and any possible workaround
  * on test device &#x23;2 (4.1.2 JB):
    * broadcasting the `SMS_RECEIVED` Intent:
      * working as expected
    * starting the `SmsReceiverService`:
      * not working:
        * does throw an Exception

#### Credits:

* [this article](https://web.archive.org/web/20120818021045/http://blog.dev001.net/post/14085892020/android-generate-incoming-sms-from-within-your-app) provides the critical code to make this work
* [this blog post](https://roshandawrani.wordpress.com/2014/06/10/android-simulating-sms-receiving-from-any-number/) uses the same code, but does a nice job adding some finishing touches

#### Legal:

* copyright: [Warren Bank](https://github.com/warren-bank)
* license: [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)
