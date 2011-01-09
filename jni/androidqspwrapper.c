#include <qsp/qsp.h>
#include <qsp/bindings/android/android.h>
#include <jni.h>
#include <string.h>
#include <android/log.h>

jobject qspCallbackObject;
JNIEnv* qspCallbackEnv;

void Java_com_qsp_player_QspPlayerStart_QSPInit(JNIEnv * env, jobject this)
{
	//__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%s]", szLogThis);
	qspCallbackObject = this;
	qspCallbackEnv = env;
	QSPInit();
}

void Java_com_qsp_player_QspPlayerStart_QSPDeInit(JNIEnv * env, jobject this)
{
	QSPDeInit();
	qspCallbackObject = NULL;
	qspCallbackEnv = NULL;
}

jboolean Java_com_qsp_player_QspPlayerStart_QSPIsInCallBack(JNIEnv * env, jobject this)
{
	return QSPIsInCallBack();
}

void Java_com_qsp_player_QspPlayerStart_QSPEnableDebugMode(JNIEnv * env, jobject this, jboolean isDebug)
{
	QSPEnableDebugMode((QSP_BOOL) isDebug);
}

jobject Java_com_qsp_player_QspPlayerStart_QSPGetCurStateData(JNIEnv * env, jobject this)
{
	//!!!STUB
	//QSPGetCurStateData(jstring *loc, (int *)actIndex, (int *)line);
	return NULL;
}

jstring Java_com_qsp_player_QspPlayerStart_QSPGetVersion(JNIEnv * env, jobject this)
{
	char * sz = qspW2C(QSPGetVersion());
	jstring result = (*env)->NewStringUTF(env, sz);
	if (sz!=NULL)
		free(sz);
	return result;
}


///* ���������� ������ ���������� ������� */
jint Java_com_qsp_player_QspPlayerStart_QSPGetFullRefreshCount(JNIEnv * env, jobject this)
{
	return QSPGetFullRefreshCount();
}
///* ------------------------------------------------------------ */
///* ������ ���� � ������������ ����� ���� */
jstring Java_com_qsp_player_QspPlayerStart_QSPGetQstFullPath(JNIEnv * env, jobject this)
{
	char * sz = qspW2C(QSPGetQstFullPath());
	jstring result = (*env)->NewStringUTF(env, sz);
	if (sz!=NULL)
		free(sz);
	return result;
}
///* ------------------------------------------------------------ */
///* �������� ������� ������� */
jstring Java_com_qsp_player_QspPlayerStart_QSPGetCurLoc(JNIEnv * env, jobject this)
{
	char * sz = qspW2C(QSPGetCurLoc());
	jstring result = (*env)->NewStringUTF(env, sz);
	if (sz!=NULL)
		free(sz);
	return result;
}
///* ------------------------------------------------------------ */
///* �������� �������� ������� */
//
///* ����� ��������� ���� �������� ������� */
jstring Java_com_qsp_player_QspPlayerStart_QSPGetMainDesc(JNIEnv * env, jobject this)
{
	char * sz = qspW2C(QSPGetMainDesc());
	jstring result = (*env)->NewStringUTF(env, sz);
	if (sz!=NULL)
		free(sz);
	return result;
}
///* ����������� ��������� ������ ��������� �������� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPIsMainDescChanged(JNIEnv * env, jobject this)
{
	return QSPIsMainDescChanged();
}
///* ------------------------------------------------------------ */
///* �������������� �������� ������� */
//
///* ����� ��������������� ���� �������� ������� */
jstring Java_com_qsp_player_QspPlayerStart_QSPGetVarsDesc(JNIEnv * env, jobject this)
{
	char * sz = qspW2C(QSPGetVarsDesc());
	jstring result = (*env)->NewStringUTF(env, sz);
	if (sz!=NULL)
		free(sz);
	return result;
}
///* ����������� ��������� ������ ��������������� �������� */
//QSP_BOOL QSPIsVarsDescChanged()
jboolean Java_com_qsp_player_QspPlayerStart_QSPIsVarsDescChanged(JNIEnv * env, jobject this)
{
	return QSPIsVarsDescChanged();
}
///* ------------------------------------------------------------ */
///* �������� �������� ���������� ��������� */
//(const QSP_CHAR *expr, QSP_BOOL *isString, int *numVal, QSP_CHAR *strVal, int strValBufSize)
jobject Java_com_qsp_player_QspPlayerStart_QSPGetExprValue(JNIEnv * env, jobject this)
{
	//!!!STUB
	//{
	//	QSPVariant v;
	//	if (qspIsExitOnError && qspErrorNum) return QSP_FALSE;
	//	qspResetError();
	//	if (qspIsDisableCodeExec) return QSP_FALSE;
	//	v = qspExprValue((QSP_CHAR *)expr);
	//	if (qspErrorNum) return QSP_FALSE;
	//	*isString = v.IsStr;
	//	if (v.IsStr)
	//	{
	//		qspStrNCopy(strVal, QSP_STR(v), strValBufSize - 1);
	//		free(QSP_STR(v));
	//		strVal[strValBufSize - 1] = 0;
	//	}
	//	else
	//		*numVal = QSP_NUM(v);
	//	return QSP_TRUE;
	//}
	return NULL;
}
///* ------------------------------------------------------------ */
///* ����� ������ ����� */
void Java_com_qsp_player_QspPlayerStart_QSPSetInputStrText(JNIEnv * env, jobject this, jstring val)
{
    const char *str = (*env)->GetStringUTFChars(env, val, NULL);
    if (str == NULL)
        return;
    QSP_CHAR * strConverted = qspC2W(str);

    QSPSetInputStrText(strConverted);

    (*env)->ReleaseStringUTFChars(env, val, str);
}
///* ------------------------------------------------------------ */
///* ������ �������� */
//
///* ���������� �������� */
jint Java_com_qsp_player_QspPlayerStart_QSPGetActionsCount(JNIEnv * env, jobject this)
{
	return QSPGetActionsCount();
}
///* ������ �������� � ��������� �������� */
//void QSPGetActionData(int ind, QSP_CHAR **image, QSP_CHAR **desc)
jobject Java_com_qsp_player_QspPlayerStart_QSPGetActionData(JNIEnv * env, jobject this, jint ind)
{
	//!!!STUB
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 1");

	char * qspImgFileName;
	char * qspActName;
	QSPGetActionData(ind, &qspImgFileName, &qspActName);
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 2");

	char * sz = qspW2C(qspActName);
	char * isz = qspW2C(qspImgFileName);
	jstring actName = (*env)->NewStringUTF(env, sz);
	jstring actImg = (*env)->NewStringUTF(env, isz);
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: qspActName[%s]", sz);
	if (sz!=NULL)
		free(sz);
	if (isz!=NULL)
		free(isz);
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 3");



	// Attempt to find the JniResult class.
	jclass clazz = (*env)->FindClass (env, "com/qsp/player/JniResult");
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 4");
	// If this class does not exist then return null.
	if (clazz == 0)
			return 0;
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 5");
	// Allocate memory for a new Version class object.  Do not bother calling
	// the constructor (the default constructor does nothing).
	jobject obj = (*env)->AllocObject (env, clazz);
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 6");
	// Attempt to find the major field.
	jfieldID fid = (*env)->GetFieldID (env, clazz, "str1", "Ljava/lang/String;");
	jfieldID fid2 = (*env)->GetFieldID (env, clazz, "str2", "Ljava/lang/String;");
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 7");
	// If this field does not exist then return null.
	if (fid == 0 || fid2 == 0)
			return 0;
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 8");
	// Set the major field to the operating system's major version.
	(*env)->SetObjectField (env, obj, fid, actName);
	(*env)->SetObjectField (env, obj, fid2, actImg);
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetActionData: 9");

	return obj;
}
///* ���������� ���� ���������� �������� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPExecuteSelActionCode(JNIEnv * env, jobject this, jboolean isRefresh)
{
	return QSPExecuteSelActionCode((QSP_BOOL)isRefresh);
}
///* ���������� ������ ���������� �������� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPSetSelActionIndex(JNIEnv * env, jobject this, jint ind, jboolean isRefresh)
{
	return QSPSetSelActionIndex(ind, (QSP_BOOL)isRefresh);
}
///* �������� ������ ���������� �������� */
jint Java_com_qsp_player_QspPlayerStart_QSPGetSelActionIndex(JNIEnv * env, jobject this)
{
	return QSPGetSelActionIndex();
}
///* ����������� ��������� ������ �������� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPIsActionsChanged(JNIEnv * env, jobject this)
{
	return QSPIsActionsChanged();
}
///* ------------------------------------------------------------ */
///* ������ �������� */
//
///* ���������� �������� */
jint Java_com_qsp_player_QspPlayerStart_QSPGetObjectsCount(JNIEnv * env, jobject this)
{
	return QSPGetObjectsCount();
}
///* ������ ������� � ��������� �������� */
//void QSPGetObjectData(int ind, QSP_CHAR **image, QSP_CHAR **desc)
jobject Java_com_qsp_player_QspPlayerStart_QSPGetObjectData(JNIEnv * env, jobject this, jint ind)
{
	//!!!STUB
	char * qspImgFileName;
	char * qspObjName;
	QSPGetObjectData(ind, &qspImgFileName, &qspObjName);

	char * sz = qspW2C(qspObjName);
	jstring objName = (*env)->NewStringUTF(env, sz);
	if (sz!=NULL)
		free(sz);

	char * isz = qspW2C(qspImgFileName);
	jstring objImg = (*env)->NewStringUTF(env, isz);
	if (isz!=NULL)
		free(isz);

	// Attempt to find the JniResult class.
	jclass clazz = (*env)->FindClass (env, "com/qsp/player/JniResult");
	// If this class does not exist then return null.
	if (clazz == 0)
			return 0;
	// Allocate memory for a new Version class object.  Do not bother calling
	// the constructor (the default constructor does nothing).
	jobject obj = (*env)->AllocObject (env, clazz);
	// Attempt to find the major field.
	jfieldID fid = (*env)->GetFieldID (env, clazz, "str1", "Ljava/lang/String;");
	jfieldID fid2 = (*env)->GetFieldID (env, clazz, "str2", "Ljava/lang/String;");
	// If this field does not exist then return null.
	if (fid == 0 || fid2 == 0)
			return 0;
	// Set the major field to the operating system's major version.
	(*env)->SetObjectField (env, obj, fid, objName);
	(*env)->SetObjectField (env, obj, fid2, objImg);

	return obj;
}
///* ���������� ������ ���������� ������� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPSetSelObjectIndex(JNIEnv * env, jobject this, jint ind, jboolean isRefresh)
{
	return QSPSetSelObjectIndex(ind, (QSP_BOOL) isRefresh);
}
///* �������� ������ ���������� ������� */
jint Java_com_qsp_player_QspPlayerStart_QSPGetSelObjectIndex(JNIEnv * env, jobject this)
{
	return QSPGetSelObjectIndex();
}
///* ����������� ��������� ������ �������� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPIsObjectsChanged(JNIEnv * env, jobject this)
{
	return QSPIsObjectsChanged();
}
///* ------------------------------------------------------------ */
///* ����� / ������� ���� */
void Java_com_qsp_player_QspPlayerStart_QSPShowWindow(JNIEnv * env, jobject this, jint type, jboolean isShow)
{
	QSPShowWindow(type, (QSP_BOOL)isShow);
}
///* ------------------------------------------------------------ */
///* ���������� */
//
///* �������� ���������� ��������� ������� */
//QSP_BOOL QSPGetVarValuesCount(const QSP_CHAR *name, int *count)
jobject Java_com_qsp_player_QspPlayerStart_QSPGetVarValuesCount(JNIEnv * env, jobject this, jstring name)
{
	//!!!STUB
	//{
	//	QSPVar *var;
	//	if (qspIsExitOnError && qspErrorNum) return QSP_FALSE;
	//	qspResetError();
	//	var = qspVarReference((QSP_CHAR *)name, QSP_FALSE);
	//	if (qspErrorNum) return QSP_FALSE;
	//	*count = var->ValsCount;
	//	return QSP_TRUE;
	//}
	return NULL;
}
///* �������� �������� ���������� �������� ������� */
//QSP_BOOL QSPGetVarValues(const QSP_CHAR *name, int ind, int *numVal, QSP_CHAR **strVal)
jobject Java_com_qsp_player_QspPlayerStart_QSPGetVarValues(JNIEnv * env, jobject this, jstring name, jint ind)
{
	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 1");
	//Convert array name to QSP string
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    if (str == NULL)
        return NULL;
    QSP_CHAR * strConverted = qspC2W(str);

//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 2");
    //Call QSP function
	int numVal = 0;
	char * strVal;
	QSP_BOOL result = QSPGetVarValues(strConverted, (int)ind, &numVal, &strVal);

//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "str: [%s]", str);
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "numval: [%d]", numVal);
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "strVal: [%s]", strVal);

//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 3");
	// Attempt to find the JniResult class.
	jclass clazz = (*env)->FindClass (env, "com/qsp/player/JniResult");
	// If this class does not exist then return null.
	if (clazz == 0)
		return NULL;
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 4");
	// Allocate memory for a new Version class object.  Do not bother calling
	// the constructor (the default constructor does nothing).
	jobject obj = (*env)->AllocObject (env, clazz);
	// Attempt to find the major field.

	jfieldID fid = (*env)->GetFieldID (env, clazz, "success", "Z");
	if (fid == 0)
		return NULL;
//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 5");
	if (result == QSP_TRUE)
	{
//		__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 6");
		(*env)->SetBooleanField (env, obj, fid, JNI_TRUE);

		char * sz = qspW2C(strVal);
		jstring jstringVal = (*env)->NewStringUTF(env, sz);
		if (sz!=NULL)
			free(sz);

		fid = (*env)->GetFieldID (env, clazz, "str1", "Ljava/lang/String;");
		// If this field does not exist then return null.
		if (fid == 0)
			return NULL;
//		__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 7");
		// Set the major field to the operating system's major version.
		(*env)->SetObjectField (env, obj, fid, jstringVal);

		jfieldID fid = (*env)->GetFieldID (env, clazz, "int1", "I");
		// If this field does not exist then return null.
		if (fid == 0)
			return NULL;
//		__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 8");
		// Set the major field to the operating system's major version.
		(*env)->SetIntField (env, obj, fid, numVal);
	}
	else
	{
//		__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 9");
		(*env)->SetBooleanField (env, obj, fid, JNI_FALSE);
	}

//	__android_log_print(ANDROID_LOG_DEBUG, "QSPDEBUG", "QSPGetVarValues: 10");
	(*env)->ReleaseStringUTFChars(env, name, str);
	return obj;
}
///* �������� ������������ ���������� ���������� */
jint Java_com_qsp_player_QspPlayerStart_QSPGetMaxVarsCount(JNIEnv * env, jobject this)
{
	return QSPGetMaxVarsCount();
}
///* �������� ��� ���������� � ��������� �������� */
//QSP_BOOL QSPGetVarNameByIndex(int index, QSP_CHAR **name)
jobject Java_com_qsp_player_QspPlayerStart_QSPGetVarNameByIndex(JNIEnv * env, jobject this, jint index)
{
	//!!!STUB
//{
//	if (index < 0 || index >= QSP_VARSCOUNT || !qspVars[index].Name) return QSP_FALSE;
//	*name = qspVars[index].Name;
//	return QSP_TRUE;
//}
	return NULL;
}
///* ------------------------------------------------------------ */
///* ���������� ���� */
//
///* ���������� ������ ���� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPExecString(JNIEnv * env, jobject this, jstring s, jboolean isRefresh)
{
    const char *str = (*env)->GetStringUTFChars(env, s, NULL);
    if (str == NULL)
        return JNI_FALSE;
    QSP_CHAR * strConverted = qspC2W(str);

    jboolean result = QSPExecString(strConverted, (QSP_BOOL)isRefresh);

    (*env)->ReleaseStringUTFChars(env, s, str);
    return result;
}
///* ���������� ���� ��������� ������� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPExecLocationCode(JNIEnv * env, jobject this, jstring name, jboolean isRefresh)
{
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    if (str == NULL)
        return JNI_FALSE;
    QSP_CHAR * strConverted = qspC2W(str);

	jboolean result = QSPExecLocationCode(strConverted, (QSP_BOOL)isRefresh);

    (*env)->ReleaseStringUTFChars(env, name, str);
    return result;
}
///* ���������� ���� �������-�������� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPExecCounter(JNIEnv * env, jobject this, jboolean isRefresh)
{
	return QSPExecCounter((QSP_BOOL)isRefresh);
}
///* ���������� ���� �������-����������� ������ ����� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPExecUserInput(JNIEnv * env, jobject this, jboolean isRefresh)
{
	return QSPExecUserInput((QSP_BOOL)isRefresh);
}
///* ------------------------------------------------------------ */
///* ������ */
//
///* �������� ���������� � ��������� ������ */
jint Java_com_qsp_player_QspPlayerStart_QSPGetLastErrorData(JNIEnv * env, jobject this)
{
	//!!!STUB
	int errorNum;
	unsigned short *dummyLoc;
	int dummyIndex;
	int dummyLine;
	//int *errorNum, QSP_CHAR **errorLoc, int *errorActIndex, int *errorLine
//	*errorNum = qspErrorNum;
//	*errorLoc = (qspErrorLoc >= 0 && qspErrorLoc < qspLocsCount ? qspLocs[qspErrorLoc].Name : 0);
//	*errorActIndex = qspErrorActIndex;
//	*errorLine = qspErrorLine;
	QSPGetLastErrorData(&errorNum, &dummyLoc, &dummyIndex, &dummyLine);
	return errorNum;
}
///* �������� �������� ������ �� �� ������ */
jstring Java_com_qsp_player_QspPlayerStart_QSPGetErrorDesc(JNIEnv * env, jobject this, jint errorNum)
{
	char * sz = qspW2C(QSPGetErrorDesc(errorNum));
	jstring result = (*env)->NewStringUTF(env, sz);
	if (sz!=NULL)
		free(sz);
	return result;
}
///* ------------------------------------------------------------ */
///* ���������� ����� */
//
///* �������� ����� ���� �� ����� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPLoadGameWorld(JNIEnv * env, jobject this, jstring fileName )
{
    const char *str = (*env)->GetStringUTFChars(env, fileName, NULL);
    if (str == NULL)
        return JNI_FALSE;

    jboolean result = QSPLoadGameWorld(str);

    (*env)->ReleaseStringUTFChars(env, fileName, str);
    return result;
}
///* �������� ����� ���� �� ������ */
jboolean Java_com_qsp_player_QspPlayerStart_QSPLoadGameWorldFromData(JNIEnv * env, jobject this, jbyteArray data, jint dataSize, jstring fileName )
{
	//converting data
	jbyte* jbuf = malloc(dataSize * sizeof (jbyte));
	if (jbuf == NULL)
		return JNI_FALSE;

	(*env)->GetByteArrayRegion(env, data, 0, dataSize, jbuf);
	int size = dataSize;
	char* mydata = (char*)jbuf;

    /* assume the prompt string and user input has less than 128
        characters */
	int fileNameLen = (*env)->GetStringLength(env, fileName) + 1;
    char buf[fileNameLen];
    const jbyte *str;
    str = (*env)->GetStringUTFChars(env, fileName, NULL);
    if (str == NULL) {
	    free(jbuf);
        return JNI_FALSE; /* OutOfMemoryError already thrown */
    }

    jboolean result = QSPLoadGameWorldFromData(mydata, size, str);
    (*env)->ReleaseStringUTFChars(env, fileName, str);

    free(jbuf);
	return result;
}
///* ���������� ��������� � ���� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPSaveGame(JNIEnv * env, jobject this, jstring fileName, jboolean isRefresh)
{
    const char *str = (*env)->GetStringUTFChars(env, fileName, NULL);
    if (str == NULL)
        return JNI_FALSE;

    jboolean result = QSPSaveGame(str, (QSP_BOOL)isRefresh);

    (*env)->ReleaseStringUTFChars(env, fileName, str);
    return result;
}
///* ���������� ��������� � ������ */
jobject Java_com_qsp_player_QspPlayerStart_QSPSaveGameAsString(JNIEnv * env, jobject this, jboolean isRefresh)
{
	//!!!STUB
//QSP_BOOL QSPSaveGameAsString(QSP_CHAR *strBuf, int strBufSize, int *realSize, QSP_BOOL isRefresh)
//{
//	int len, size;
//	QSP_CHAR *data;
//	if (qspIsExitOnError && qspErrorNum) return QSP_FALSE;
//	qspPrepareExecution();
//	if (qspIsDisableCodeExec) return QSP_FALSE;
//	if (!(len = qspSaveGameStatusToString(&data)))
//	{
//		*realSize = 0;
//		return QSP_FALSE;
//	}
//	size = len + 1;
//	*realSize = size;
//	if (size > strBufSize)
//	{
//		free(data);
//		return QSP_FALSE;
//	}
//	qspStrNCopy(strBuf, data, strBufSize - 1);
//	free(data);
//	strBuf[strBufSize - 1] = 0;
//	if (isRefresh) qspCallRefreshInt(QSP_FALSE);
//	return QSP_TRUE;
//}
	return NULL;
}
///* �������� ��������� �� ����� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPOpenSavedGame(JNIEnv * env, jobject this, jstring fileName, jboolean isRefresh)
{
    const char *str = (*env)->GetStringUTFChars(env, fileName, NULL);
    if (str == NULL)
        return JNI_FALSE;

    jboolean result = QSPOpenSavedGame(str, (QSP_BOOL)isRefresh);

    (*env)->ReleaseStringUTFChars(env, fileName, str);
    return result;
}
///* �������� ��������� �� ������ */
jobject Java_com_qsp_player_QspPlayerStart_QSPOpenSavedGameFromString(JNIEnv * env, jobject this, jstring str, jboolean isRefresh)
{
	//!!!STUB
	//QSP_BOOL QSPOpenSavedGameFromString(const QSP_CHAR *str, QSP_BOOL isRefresh)
	//{
//	if (qspIsExitOnError && qspErrorNum) return QSP_FALSE;
//	qspPrepareExecution();
//	if (qspIsDisableCodeExec) return QSP_FALSE;
//	qspOpenGameStatusFromString((QSP_CHAR *)str);
//	if (qspErrorNum) return QSP_FALSE;
//	if (isRefresh) qspCallRefreshInt(QSP_FALSE);
//	return QSP_TRUE;
	return NULL;
}
///* ���������� ���� */
jboolean Java_com_qsp_player_QspPlayerStart_QSPRestartGame(JNIEnv * env, jobject this, jboolean isRefresh)
{
	return QSPRestartGame((QSP_BOOL)isRefresh);
}
///* ------------------------------------------------------------ */
///* ���� */
///* �-� ������������� ������ ��� ������ �� CallBack'� QSP_CALL_SHOWMENU */
void Java_com_qsp_player_QspPlayerStart_QSPSelectMenuItem(JNIEnv * env, jobject this, jint index)
{
	QSPSelectMenuItem(index);
}
///* ------------------------------------------------------------ */
///* ��������� CALLBACK'�� */
//void QSPSetCallBack(int type, QSP_CALLBACK func)
//{
//	qspSetCallBack(type, func);
//}
