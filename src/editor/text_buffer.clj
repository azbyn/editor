(ns editor.text-buffer
  (:require
   [seesaw.core :refer :all]
   [seesaw.graphics :refer :all]
   [seesaw.color :refer [color]])
  )

(defn ->Pos [x y]
  {:x x :y y})

;; buffer {
;;   :type :text / :drawing / ...
;;   :lines [ "bla", "bla" ]
;;   :cursorPos {:x x, :y y}
;; ...
;; }

;; make pos a record?

;; text-buffer-options {
;;   :linewrap? false
;;   :linenumbers? true
;;   :cursor-type :block / :line / :half-line / ...
;;   :cursor-blink? false
;;   :font "idk"
;;  ...
;; }

(defn create-text-buffer []
  {:type :text
   :lines ["change", "me", "самогон"]
   :cursorPos (->Pos 0 0)
   })

;;TODO move me
(def editor-options {:linewrap? false
                     :linenumbers? true ;TODO
                     :cursor-type :block
                     :cursor-blink? false

                     :font (seesaw.font/font "DejaVu Sans Mono 20")
                     :background (color "#1D1F21")
                     :foreground (color "#E0E0E0")
                     :caret-color (color "#FBA922")
                     })

(defn draw-text-buffer
  ([canvas g buffer] (draw-text-buffer canvas g buffer editor-options))
  ([canvas ^java.awt.Graphics g buffer options]
   (let [w          (width canvas)
         h          (height canvas)
         font (:font options)
         
         metrics (.getFontMetrics g font)
         
         char-height (.getHeight metrics)

         x-offset 5]


     ;; (draw g
     (doto g
       (.setColor (:background options))
       (.fillRect 0 0 w h)


       (.setFont font)
       (.setColor (:foreground options))

       ;; (.drawString "hello" 50 char-height)
       )
     ;; (println "metrics" metrics)

     ;; (let [string "hello"
     ;;       ctx (.getFontRenderContext g)
     ;;       metrics (.getLineMetrics font "bla|" ctx)]
     ;;   )
     
     (doseq [[ln value] (map-indexed vector (:lines buffer))]
       (let [string (str (+ 1 ln) " " value)
             ;; width (.stringWidth string)
             ]
         (.drawString g string x-offset (* (+ 1 ln) char-height))
         ))
     )
   ))
