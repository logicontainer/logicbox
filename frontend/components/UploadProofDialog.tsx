
'use client';
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"

import { useState } from 'react';

export default function UploadProofDialog() {
  const [jsonContent, setJsonContent] = useState(null);
  const [error, setError] = useState('');

  const handleFileChange = async (e) => {
    setError('');
    const file = e.target.files[0];

    if (!file) return;
    if (file.type !== 'application/json') {
      setError('Please upload a valid JSON file.');
      return;
    }

    try {
      const text = await file.text();
      const parsed = JSON.parse(text);  // will throw if invalid JSON
      setJsonContent(parsed);
    } catch {
      setError('Invalid JSON file.');
    }
  };

  return (
    <div className="">
      <div className="grid w-full max-w-sm items-center gap-3">
        <Label htmlFor="proof-file">Proof file</Label>
        <Input id="proof-file" type="file"
          accept=".json,application/json"
          onChange={handleFileChange}
        />
      </div>
      {error && <p className="text-red-600">{error}</p>}
    </div>
  );
  // return (
  //   <Button variant="outline">
  //     <UploadIcon />
  //   </Button>
  // )
}
