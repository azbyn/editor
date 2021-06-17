(ns editor.core
  (:gen-class)

  (:import
   [javax.swing SwingUtilities JFrame JLabel JPanel]
   [java.awt GraphicsEnvironment GraphicsDevice Color])

  ;; (:import
   ;; [java.awt Color])
  (:use clojure.pprint)
  (:use seesaw.core)
  (:use seesaw.dev)
  )

;; show-events
;; show-options
;; for the repl:
;; (use 'editor.core :reload-all)
;; https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html

(defn make-transparent!
  [root]

  (doto root
      (.setUndecorated true)
      (.setBackground (Color. 0 0xff 0 50)))

  ;; (let [pane (.getContentPane root)]
  ;;   (doto pane
  ;;     (.setOpaque true)
  ;;     (.setBackground (new java.awt.Color 0 0 0xff 0x7f))
  ;;     )
  ;;   )

  root)

(defn on-test-event [e]
  (let [key (.getKeyChar e)
        extendedCode (.getExtendedKeyCode e)
        modifiers (.getModifiers e)]
    (println "key-down:" key "-" extendedCode ";" modifiers)
    (when (and (= extendedCode (int \Q)) ;;(= key \q)
               (= modifiers 2))
      (println "quit")
      )
    ))

(defn add-keys! [root]
  (let [a-test (action :handler on-test-event :key "q" :name "Test")
        ])
  root)

;; (def uberframe (atom nil))
;; deref/@

(defn gui []
  (def [uberframe
        (frame :title "Editor"
               ;; :class
               :content (border-panel
                         :north "oida"
                         :south "bre"
                         ;; :background (Color. 0xff 0 0 0x2d)
                         )
               :listen [:key-pressed #'on-test-event ;; (fn [e] (println "pressed" e))
                        ]
               ;; :background :blue
               :on-close :dispose ;;:exit
               )]

    (-> uberframe
        make-transparent!

        ;; add-keys!
        
        pack!
        show!)))

(defn oida []
  (println "noida"))

(defn -main [& args]
  ;; (oida)
  (load-file "src/editor/test.clj")
  (oida)

  ;; (let [thing (new editor.core.PicturePanel)])
  
  (invoke-later
   (gui)

   (println "Koniec!")
   )
  ;; (println uberframe)
  )
