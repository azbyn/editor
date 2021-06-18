(ns editor.core
  (:gen-class)

  (:import
   [javax.swing SwingUtilities JFrame JLabel JPanel]
   [java.awt GraphicsEnvironment GraphicsDevice Color Font]
   [java.awt.event KeyEvent]
   ;; [javla LineNumberingTextArea]
   )
  (:require
   [seesaw.core :as ss]
   [editor.utils :refer [case-with-eval case-enum]]
   [editor.text-buffer :refer :all]
   ;; [seesaw.color]
   )
  
  ;; (:import
   ;; [java.awt Color])
  (:use clojure.pprint)
  (:use seesaw.color)
  (:use seesaw.dev)
  )

(use 'seesaw.font)
(use 'seesaw.core)

;; show-events
;; show-options
;; for the repl:
;; (use 'editor.core :reload-all)

;; https://cemerick.com/blog/2011/07/05/flowchart-for-choosing-the-right-clojure-type-definition-form.html



(def the-gui (atom nil))
(def the-state (atom nil))

(defn get-gui
  "Get the current gui (JFrame)"
  []
  @the-gui)

(defn get-current-buffer []
  (:current-buffer @the-state))

;; (defn the-gui-get-current-canvas []
;;   (:current-canvas @the-state))




(defn init-state! []
  ;; (let [buffer (cre)]
  (reset! the-state {:current-buffer (create-text-buffer)
                     ;; TODO multiple buffers
                     ;; :buffers []

                     ;; TODO multiple canvases
                     ;; might not be needed
                     ;; :current-canvas nil
                     }))

;; https://github.com/clj-commons/seesaw/tree/develop/test/seesaw/test/examples



(defn paint
  "The default canvas paint function
  
  we should pass an index or smth to differentiate between canvases
  or somehow deduce what window this is (with :id perhaps (on canvas))
  "
  [canvas g]

  (draw-text-buffer canvas g (get-current-buffer)))



;; https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html

(defn make-transparent!
  [root]

  (doto root
      (.setUndecorated true)
      (.setBackground (color 0 0xff 0 50)))
  root)

(defn quit! []
  (dispose! (get-gui)))

(defn move-cursor! [dx]
  ;; (let [pane (get-current-pane)
  ;;       pos (.getCaretPosition pane)]
  ;;   (.setCaretPosition pane (+ dx pos))
  ;;   )
  ;;TODO
  nil
  )

(defn forward-char! []
  (move-cursor! 1)
  (println "forward-char")
  )

(defn backward-char! []
  (move-cursor! -1)
  (println "background-char"))

(def mod-ctrl 2)
(def mod-shift 1)

(defn on-key-pressed [e]
  (let [key (.getKeyChar e)
        extendedCode (.getExtendedKeyCode e)
        modifiers (.getModifiers e)]
    ;; (println e)
    (println "key-down:" key "-" extendedCode ";" modifiers)
    (when (= modifiers mod-ctrl)
      (case-with-eval extendedCode
        KeyEvent/VK_Q (quit!)
        KeyEvent/VK_F (forward-char!)
        KeyEvent/VK_A (backward-char!)
        ;; (println "other key" extendedCode)
        nil))
    ;; (when (and (= extendedCode (int \Q)) ;;(= key \q)
    ;;            (= modifiers 2))
    ;;   (dispose! (get-gui))
    ;;   ;; (println "quit")
    ;;   )
    )
  true)


(defn create-editor-panel []
  (canvas :paint paint
          :background (color 0 0 0 0))
  ;; (ss/scrollable
  ;; (LineNumberingTextArea. ;; JTextLineNumber.
   ;; (editor-pane
   ;; ;; (text
   ;;  ;; :editable? true
   ;;  ;; :content-type "text/"
   ;;  :text "Привіт"
   ;;  ;; :editable? false
   ;;  ;; :multi-line? true
   ;;  ;; :tab-size 4

   ;;  :font (font "DejaVu Sans Mono 20"
   ;;              ;; :style #{:bold}
   ;;              )

   ;;  :listen [;;:key-typed #'on-key-pressed
   ;;           :key-pressed #'on-key-pressed]
   ;;  :background "#1D1F21"
   ;;  :foreground "#E0E0E0"
   ;;  :caret-color "#FBA922"
   ;;  :selection-color :blue;; (color 0x70 0x70 0x70 20)
   ;;  ;; ;; :tip "add salt to pasta while boiling"
   ;;  ;; ;; :background (color 255 0 0 25)
   ;;  :border 5
   ;;  )
  ;; )
   )

(defn create-frame-contents []
  (let [value (ss/border-panel
               :center (create-editor-panel)
               :background "#ff0000"
               :border 5
               )]
    (swap! the-state assoc :current-page value)
    value))

;; (swap! uses a function)
(defn init-gui! []
  (reset! the-gui
          (frame :title "Editor"
                 ;; :class
                 :content (create-frame-contents)
                 ;; :listen [:key-pressed #'on-key-pressed]
                 ;; :background :blue
                 :on-close :dispose ;;:exit
                 :width 800
                 :height 600
                 ))

    (-> (get-gui)
        make-transparent!

        ;; add-keys!
        
        pack!
        show!))

(defn oida []
  (println "noida"))

(defn -main [& args]
  (init-state!)
  ;; (oida)
  (load-file "src/editor/test.clj")
  (oida)

  ;; (let [thing (new editor.core.PicturePanel)])
  
  (invoke-later
   (init-gui!)

   (println "Koniec!")
   )
  ;; (println uberframe)
  )
