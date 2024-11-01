import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Overall from './components/overall/Overall'
import Searchbar from './components/searchbar/Searchbar'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>

      <Overall />
      <Searchbar onSearch={(query) => console.log(query)} />
    </>
  )
}

export default App
