(ns editor.core
  (:gen-class)

  (:import
   [javax.swing SwingUtilities JFrame JLabel JPanel]
   [java.awt GraphicsEnvironment GraphicsDevice Color])

  ;; (:import
   ;; [java.awt Color])
  (:use clojure.pprint)
  (:use seesaw.core)
  )

;; (gen-class
;;  :extends javax.swing.JPanel
;;  :name editor.core.BackgroundPanel
;;  ;; :main false
;;  :exposes-methods {paintComponent parentPaintComponent}
;;  :prefix "background-panel-")
  


;; (defn create-and-show-gui
;;   []

;;   (let [my-frame (doto (JFrame. "My Frame")
;;                    (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE))
;;         my-label (JLabel. "Hello UI")
;;         content-pane (.getContentPane  my-frame)]

;;     (.add content-pane my-label)
;;     (.pack my-frame)
;;     (.setVisible my-frame true)))


;; for the repl:
;; (use 'editor.core :reload-all)
;; https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html

(defn add-behaviors!
  [root]

  ;; (root .se,)

  ;; (let [pane (.setContentPane (new editor.code.PicturePanel))])
  ;; (pprint root)
  
  (let [pane (.getContentPane root)]
    (doto pane
      (.setOpaque true)
      (.setBackground (new java.awt.Color 0 0 0xff 0x7f))
      ;; (.setBackground pane (new java.awt.Color 0 0 0xff 0xff))
      )
    )
  ;; (make-transparent! root)
  ;; (pprint )

  root)

;; (def uberframe (atom nil))


(defn uberpaint [this g]
  (println "g" g)
  ;; (println "c" this)
  ;; (let [g2d])
  )

(defn gui []
  ;; (swap! uberframe
  ;; (JFrame/setDefaultLookAndFeelDecorated true)

  ;; (def content (proxy [JPanel] []
  ;;                (paintComponent [g];; (println "kurac"))
  ;;                  )
                 ;; ))
  ;; (doto content
  ;;   ;; (.setOpaque false)
  ;;   (.setBackground (Color. 0 0 0xff 0x7f))
  ;;   )
  
  (def uberframe
    (frame :title "Re Editor"
           ;; :class
           :content ;content
           (border-panel
                     :north "oi"
                     :south "daz"
                     :background (Color. 0xff 0 0 0x2d)
                     )
           ;; :background :blue
           :on-close :dispose ;;:exit
           ))
  (doto uberframe
    (.setUndecorated true)
    (.setBackground (Color. 0 0 0 0)))
  
  ;; (SwingUtilities/invokeLater create-and-show-gui)
  (-> uberframe
      add-behaviors!
      pack!
      show!)
  )

(defn oida []
  (println "noida"))

(defn -main [& args]
  
  ;; (oida)
  (load-file "src/editor/test.clj")
  (oida)

  ;; (let [thing (new editor.core.PicturePanel)])
  
  (invoke-later
   (gui))
  ;; (println "Hello, World!")
  )
