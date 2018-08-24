// Typedefs
typedef int uint16;
typedef int uint;
typedef short int16;
typedef char byte;
typedef char int8;

// Prototypes.
byte bgetc(void);
int SyncRx(int8 in, int16 *out);
void sendInteger(int16 x);
int16 hpFilt(int16 datum, int init);
int16 lpFilt(int16 datum , int init);
int16 derivate(int16 x0, int init);
int16 movingWindowIntegration(int16 datum, int init);
int16 qrsDetection(int16, int init);
int16 Peak( int16 datum, int init );
void UpdateQ(int16 newQ);
void UpdateRR(int16 newRR);
void UpdateN(int16 newN);
int16 getDetectionThreshold(void);
int16 getQrsSum(void);
int16 getNoiseSum(void);
void setPause(char);