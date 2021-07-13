(ns editor.buffer
  "Holds an awt component and the state necesary for it"
  )

;; (defrecord Buffer
;;     [])

(defn create-buffer
  "Create a buffer.
  This represents a wrapper around a `java.awt.Component`.

  You shouldn't call this directly unless you want to create a new
  kind of buffer. `create-text-buffer` and friends should be enough
  for normal use.

  Mandatory paramters:
  `:type` - The type of the buffer. `:text` for example.
    - TODO not used now
  `:component` - The drawn component. Must inherit from `java.awt.Component`.
  
  Optional paramters:
  - `:on-paint`: `buffer -> nil`
    - see `on-paint!`
  - `:on-component-added`: `buffer -> nil`

  
  - more to be added in the future..

  TODO spec. (check if :type and component are set)
  - Other key-value pairs are also added in the buffer.
  "
  [& {:as args}]
  args)

;;TODO a get-component function?

;; (defn get-component
;;   "Get the `java.awt.Component` of the buffer
  
;;   For internal usage."
;;   [buffer]
;;   (:component buffer))

(defn on-paint!
  "Called in `repaint-buffer!` - tl;dr when something must change on screen.
  
  For internal usage."
  [buffer]
  (when-let [impl (:on-paint buffer)]
    (impl buffer)))

(defn on-component-added!
  "Called after the buffer's component gets added.
  Used for things like `.createBufferStrategy` for a `java.awt.Canvas`
  
  For internal use."
  [buffer]
  (when-let [impl (:on-component-added buffer)]
    (impl buffer)))
