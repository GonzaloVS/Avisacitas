# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.google.api.client.http.GenericUrl
-dontwarn com.google.api.client.http.HttpHeaders
-dontwarn com.google.api.client.http.HttpRequest
-dontwarn com.google.api.client.http.HttpRequestFactory
-dontwarn com.google.api.client.http.HttpResponse
-dontwarn com.google.api.client.http.HttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport$Builder
-dontwarn com.google.api.client.http.javanet.NetHttpTransport
-dontwarn java.lang.reflect.AnnotatedType
-dontwarn org.joda.time.Instant
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn javax.script.ScriptEngine
-dontwarn javax.script.ScriptEngineManager

-dontwarn com.sun.jna.FunctionMapper
-dontwarn com.sun.jna.JNIEnv
-dontwarn com.sun.jna.Library
-dontwarn com.sun.jna.Native
-dontwarn com.sun.jna.NativeLibrary
-dontwarn com.sun.jna.Platform
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings
-dontwarn java.awt.Canvas
-dontwarn java.awt.Color
-dontwarn java.awt.Component
-dontwarn java.awt.DisplayMode
-dontwarn java.awt.Font
-dontwarn java.awt.FontFormatException
-dontwarn java.awt.Graphics
-dontwarn java.awt.GraphicsConfiguration
-dontwarn java.awt.GraphicsDevice
-dontwarn java.awt.Image
-dontwarn java.awt.LayoutManager
-dontwarn java.awt.Point
-dontwarn java.awt.Window
-dontwarn java.awt.color.ColorSpace
-dontwarn java.awt.event.ActionEvent
-dontwarn java.awt.event.ActionListener
-dontwarn java.awt.event.ComponentAdapter
-dontwarn java.awt.event.ComponentEvent
-dontwarn java.awt.event.ComponentListener
-dontwarn java.awt.event.FocusEvent$Cause
-dontwarn java.awt.event.HierarchyEvent
-dontwarn java.awt.event.HierarchyListener
-dontwarn java.awt.event.InputMethodEvent
-dontwarn java.awt.event.InputMethodListener
-dontwarn java.awt.event.KeyAdapter
-dontwarn java.awt.event.KeyEvent
-dontwarn java.awt.event.KeyListener
-dontwarn java.awt.event.MouseAdapter
-dontwarn java.awt.event.MouseEvent
-dontwarn java.awt.event.MouseListener
-dontwarn java.awt.event.MouseMotionAdapter
-dontwarn java.awt.event.MouseMotionListener
-dontwarn java.awt.event.MouseWheelEvent
-dontwarn java.awt.event.MouseWheelListener
-dontwarn java.awt.geom.AffineTransform
-dontwarn java.awt.im.InputMethodRequests
-dontwarn java.awt.image.BufferedImage
-dontwarn java.awt.image.ColorModel
-dontwarn java.awt.image.ComponentColorModel
-dontwarn java.awt.image.DataBuffer
-dontwarn java.awt.image.DataBufferByte
-dontwarn java.awt.image.ImageObserver
-dontwarn java.awt.image.Raster
-dontwarn java.awt.image.WritableRaster
-dontwarn java.lang.ProcessHandle
-dontwarn java.lang.instrument.ClassDefinition
-dontwarn java.lang.instrument.IllegalClassFormatException
-dontwarn java.lang.instrument.UnmodifiableClassException
-dontwarn java.lang.invoke.MethodHandleProxies
-dontwarn javax.accessibility.Accessible
-dontwarn javax.accessibility.AccessibleContext
-dontwarn javax.swing.JComponent
-dontwarn javax.swing.JPanel
-dontwarn javax.swing.JRootPane
-dontwarn javax.swing.SwingUtilities
-dontwarn javax.swing.Timer
-dontwarn javax.swing.UIManager

-printusage usage.txt

#IMPLEMENTATIONS

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}

# Librerías de terceros
-keep class com.intuit.sdp.** { *; }
-keep class com.intuit.ssp.** { *; }
-keep class com.makeramen.** { *; }
-keep class com.googlecode.libphonenumber.** { *; }
-keep class com.hbb20.** { *; }


-keep class opennlp.** { *; }
-keep class javax.** { *; }
-keepclassmembers class * {
    javax.xml.parsers.SAXParserFactory newSAXParserFactory();
}



# Jackson y Gson
-keep class com.fasterxml.jackson.** { *; }
-keep class com.google.code.gson.** { *; }

# Volley
-keep class com.android.volley.** { *; }

-keep class * {*;}

# **************************************************************************************************

#-keep class com.gvs.avisacitas.model.contacts.** { *; }
#-keep class com.gvs.avisacitas.entryPoints.ui.login.LoginWapinFragment { *; }
#-keep class com.gvs.avisacitas.entryPoints.ui.login.LoginWapinViewModel { *; }
#-keep class com.gvs.avisacitas.model.sharedPreferences.** { *; }