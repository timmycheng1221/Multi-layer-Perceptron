# Multi-layer-Perceptron
## 程式簡介 
- src/Main.java：  
建立主要的GUI介面，先將讀入的資料夾依照期望值做排序，並利用  
![Formula](https://github.com/timmycheng1221/Multi-layer-Perceptron/blob/master/images/f1.png)  
函數逼近的方式將期望輸出正規化成[0, 1]之間方便多層感知機做處理，若資料集的數量少於10筆，則不會將資料集分成訓練和測試兩種，全部資料集將都會進行訓練。
- src/MultiPerception.java：  
多層感知機訓練前先隨機初始化各神經元的鍵結值，範圍為[-1, 1]，閥值為-1，在迭代次數內或未達到訓練準確率時：
  - 前饋階段：資料集進入網路後於輸出層計算出一個值，若這個值不在期望輸出的範圍內，則進入倒傳遞階段。
  - 倒傳遞階段：從最後一顆神經元的輸出往第一個神經元輸出計算出各神經元的delta值，進入到調整鍵結值階段。
  - 調整鍵結值階段： 依照講義公式調整鍵結值。
  訓練完成後將資料集和鍵結值傳到Plot.java進行繪圖工作。
- src/Plot.java：  
繪製資料集(訓練+測試)的點、多層感知機訓練後的點和空間轉換後的點。  
## 程式執行說明
- 一開始的畫面  
  
![Image](https://github.com/timmycheng1221/Multi-layer-Perceptron/blob/master/images/a1.jpg)
- 按下右上角的「Open File」→匯入資料集→「Input」方框顯示資料集  
  
![Image](https://github.com/timmycheng1221/Multi-layer-Perceptron/blob/master/images/a2.jpg)
- 輸入學習率、訓練準確率和迭代次數，選擇隱藏層層數(1 ~ 4)和各隱藏層的神經元數(2 ~ 9)，輸出層固定為1顆神經元，並按下右下角的「Train」→上圖顯示訓練資料集的期望輸出，下圖顯示訓練結果(圖形、右下角的「Training Accuracy」顯示訓練準確率、「Weight」顯示訓練後各神經元的鍵結值、「RMSE」方框顯示訓練過程中均方根誤差的變化)。  
  
![Image](https://github.com/timmycheng1221/Multi-layer-Perceptron/blob/master/images/a3.jpg)
- 按下右下角的「Test」，上圖顯示測試資料集的期望輸出，下圖顯示測試結果(圖形、左下角的「Testing Accuracy」顯示測試準確率)。  
  
![Image](https://github.com/timmycheng1221/Multi-layer-Perceptron/blob/master/images/a4.jpg)
- 按下右下角的「Transformation」，若最後一個隱藏層的神經元為兩顆，則可以將資料集做空間轉換成二維資料，上圖顯示訓練結果(圖形)，下圖顯示測試結果(圖形)。 
  
![Image](https://github.com/timmycheng1221/Multi-layer-Perceptron/blob/master/images/a5.jpg)
