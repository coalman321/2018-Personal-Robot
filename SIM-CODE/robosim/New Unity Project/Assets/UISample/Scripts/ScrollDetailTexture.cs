using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(Image))]
public class ScrollDetailTexture : MonoBehaviour
{
    private Material m_Mat;

    private Matrix4x4 m_Matrix;
    private Material mCopy;
    private Material mOriginal;
    private Image mSprite;
    public Vector2 scrollPerSecond = Vector2.zero;
    public bool uniqueMaterial;

    private void OnEnable()
    {
        mSprite = GetComponent<Image>();
        mOriginal = mSprite.material;

        if (uniqueMaterial && mSprite.material != null)
        {
            mCopy = new Material(mOriginal);
            mCopy.name = "Copy of " + mOriginal.name;
            mCopy.hideFlags = HideFlags.DontSave;
            mSprite.material = mCopy;
        }
    }

    private void OnDisable()
    {
        if (mCopy != null)
        {
            mSprite.material = mOriginal;
            if (Application.isEditor)
                DestroyImmediate(mCopy);
            else
                Destroy(mCopy);
            mCopy = null;
        }

        mOriginal = null;
    }

    private void Update()
    {
        var mat = mCopy != null ? mCopy : mOriginal;

        if (mat != null)
        {
            var tex = mat.GetTexture("_DetailTex");

            if (tex != null) mat.SetTextureOffset("_DetailTex", scrollPerSecond * Time.time);
        }
    }
}